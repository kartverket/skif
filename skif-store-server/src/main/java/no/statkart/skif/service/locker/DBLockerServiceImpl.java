package no.statkart.skif.service.locker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.exception.OracleBatchUpdateCountException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.util.JDBCHelper;
import no.statkart.skif.util.OracleUtils;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class DBLockerServiceImpl implements DBLockerService<Long> {
    private static Logger logger = LoggerFactory.getLogger(DBLockerServiceImpl.class);

    final Provider<Connection> connectionProvider;
    final Configuration configuration;

    private static long dbMillisecDifference;
    private static boolean dbMilliescDifferenceInitialized;
    private static final long MAX_TIME_DIFF_THRESHOLD = 1000 * 60 * 10; // 10 Minutter
    private static final int MAX_BATCH_SIZE = 100;

    @Inject
    public DBLockerServiceImpl(Provider<Connection> connectionProvider, Configuration configuration) {
        this.connectionProvider = connectionProvider;
        this.configuration = configuration;
    }

    @Override
    public LockInfo<Long> lock(LockKey<Long> lockKey, String owner, long lockTimeout) throws LockedException {
        Connection con = connectionProvider.get();
        Timestamp expires = calcExpiration(con, lockTimeout);

        LockInfo<Long> lockInfo;

        boolean rollback = true;
        try {
            // Anta at låsen ikke finnes. Gjør en insert
            lockInfo = insertLock(con, lockKey, owner, expires);
            if (lockInfo == null) {
                // Lås finnes. Sjekk om bruker allerede har låsen eller den kan times ut.
                lockInfo = getLock(con, lockKey);
                if (lockInfo == null) {
                    // Race condition: Kan ikke opprette eller finne lås. Lite sannsynlig at dette skal oppstå
                    throw new LockedException(owner, new LockInfo<>(lockKey, null));
                } else if (lockInfo.isOwnedBy(owner)) {
                    lockInfo = renewLock(con, lockInfo, expires);
                } else if (lockInfo.expired()) {
                    lockInfo = timeoutAndTakeLock(con, owner, expires, lockInfo);
                } else {
                    throw new LockedException(owner, lockInfo);
                }
            }

            con.commit();
            rollback = false;
        } catch (SQLException e) {
            throw new OperationalException(e);
        } finally {
            if (rollback) {
                JDBCHelper.rollback(con);
            }
        }

        return lockInfo;

    }

    @Override
    public Set<LockInfo<Long>> lockAll(Set<LockKey<Long>> lockKeys, String owner, long lockTimeout) throws LockedException {
        boolean rollback = false;
        Connection con = connectionProvider.get();
        Timestamp expires = calcExpiration(con, lockTimeout);
        Set<LockInfo<Long>> result;
        try {
//            con.setAutoCommit(false);
            Set<LockInfo<Long>> insertedLocks = insertLocks(con, lockKeys, owner, expires);
            if (insertedLocks != null) {
                result = insertedLocks;
            } else {
                // Kunne ikke låse alle i første forsøk. Finn ut hvilke som finnes fra før og timeout låse som er expired.
                result = new HashSet<>();
                Set<LockInfo<Long>> existingLocks = findExistingLocks(con, lockKeys);
                Set<LockInfo<Long>> locksNotOwnedByKey = getLocksNotOwnedByKey(existingLocks, owner);
                // Sjekk at alle låse vi ikke eier kan times ut
                verifyAllLocksExpired(locksNotOwnedByKey, owner);
                Set<LockKey<Long>> idsWithNoLock = getIdsWithNoLock(lockKeys, existingLocks);
                insertedLocks = insertLocks(con, idsWithNoLock, owner, expires);
                if (insertedLocks == null) {
                    // Race condition: Fikk ikke lov å opprettet låse, noen av dem må vært tatt av anden bruker akkurat nå
                    existingLocks = findExistingLocks(con, idsWithNoLock);
                    throw new LockedException(owner, existingLocks);
                }

                // Renew låse vi allerede selv eier
                Set<LockInfo<Long>> locksToRenew = existingLocks;
                locksToRenew.removeAll(locksNotOwnedByKey);
                Set<LockInfo<Long>> renewedLocks = renewLocks(con, locksToRenew, expires, owner);

                // Ta låse som ikke eies av OWNER, men som muligvis kan times ut
                Set<LockInfo<Long>> timedoutLocks = timeoutAndTakeLocks(con, locksNotOwnedByKey, owner, expires);

                result.addAll(insertedLocks);
                result.addAll(renewedLocks);
                result.addAll(timedoutLocks);
            }
            con.commit();
            return result;
        } catch (RuntimeException e) {
            rollback = true;
            throw e;
        } catch (SQLException e) {
            rollback = true;
            throw new OperationalException(e);
        } finally {
            if (rollback) {
                JDBCHelper.rollback(con);
            }
        }
    }

    @Override
    public void unlock(LockKey<Long> lockKey, String owner) {
        Connection con = connectionProvider.get();

        boolean rollback = true;
        String sqlString = "DELETE FROM " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " WHERE ID=? AND CLASS=? AND OWNER=?";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            stmt.setLong(1, lockKey.keyValue);
            stmt.setString(2, lockKey.discriminator);
            stmt.setString(3, owner);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL: " + sqlString);
                logger.debug("SQL: PARAM 1=" + lockKey.keyValue);
                logger.debug("SQL: PARAM 2=" + lockKey.discriminator);
                logger.debug("SQL: PARAM 3=" + owner);
            }
            stmt.executeUpdate();

            con.commit();
            rollback = false;
        } catch (SQLException e) {
            throw new OperationalException(e);
        } finally {
            if (rollback) {
                JDBCHelper.rollback(con);
            }
        }
    }

    @Override
    public void unlockAll(Set<LockKey<Long>> unLockKeys, String owner) {
        boolean rollback = false;
        Connection con = connectionProvider.get();
        try {
//            con.setAutoCommit(false);
            unlockAll(con, unLockKeys, owner);
            con.commit();
        } catch (RuntimeException | SQLException e) {
            rollback = true;
            throw new ImplementationException("Failed to unlock objects", e, logger);
        } finally {
            if (rollback) {
                JDBCHelper.rollback(con);
            }
        }
    }

    @Override
    public Collection<LockInfo<Long>> getLocksBy(String owner) {
        Connection con = connectionProvider.get();
        return getLocksBy(con, owner);
    }

    @Override
    public void releaseAllLocks(String owner) {
        Connection con = connectionProvider.get();

        boolean rollback = true;
        String sqlString = "DELETE FROM " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " WHERE OWNER=?";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            stmt.setString(1, owner);
            logger.debug("SQL: " + sqlString);
            logger.debug("SQL: PARAM 1=" + owner);
            stmt.executeUpdate();
            con.commit();
            rollback = false;
        } catch (SQLException e) {
            throw new OperationalException("Deleting all locks for user failed: " + owner, e);
        } finally {
            if (rollback) {
                JDBCHelper.rollback(con);
            }
        }

    }

    @Override
    public Collection<LockInfo<Long>> renewAllLocks(String owner, long lockTimeout) {
        Connection con = connectionProvider.get();
        Timestamp expires = calcExpiration(con, lockTimeout);
        boolean rollback = true;
        try {
            renewAllLocks(con, owner, expires);
            con.commit();
            rollback = false;
        } catch (SQLException e) {
            throw new OperationalException(e);
        } finally {
            if (rollback) {
                JDBCHelper.rollback(con);
            }
        }
        return getLocksBy(con, owner);
    }

    @Override
    public LockInfo<Long> getLock(LockKey<Long> lockKey) {
        Connection con = connectionProvider.get();
        return getLock(con, lockKey);
    }

    /**
     * Beregner utløpstidspunkt for lås. Tar hensyn til tidsforskjell mellom server og database slik at lockTimeout
     * justeres dersom appserveren har en klokke som er tidligere enn databasens. Motsatt vei gjøres ingen justering.
     *
     * @param con         databaseforbindelse
     * @param lockTimeout låse periode i millisekunder
     * @return beregnet utløpstidspunkt
     * @throws OperationalException hvis tidsforskjellen mellom appserver og databaseserver er størren enn {@link #MAX_TIME_DIFF_THRESHOLD}
     */
    private Timestamp calcExpiration(Connection con, long lockTimeout) throws OperationalException {
        long diff = getDBMillisecDifference(con);
        return new Timestamp(System.currentTimeMillis() + lockTimeout + diff);
    }

    /**
     * Beregner tidsforskjelljustering mellom database og server og returnerer denne.
     * <p>
     * Dersom serveren har en tid som er tidligere enn databasens returneres denne forskjellen, med mindre
     * tidsforskjellen er vesentlig (MAX_TIME_DIFF_THRESHOLD). I dette tilfelle kastes en exception.
     * <p>
     * Hvis serveren har en tid som er senere enn databasen returneres 0. Hvis tidsforskjellen er vesentlig
     * (MAX_TIME_DIFF_THRESHOLD) logges en feilmelding men det kastes ingen exception.
     * <p>
     * Forskjellen beregnes kun en gang, da forskjellen antas å være konstant.
     *
     * @param con databaseforbindelse
     * @return tidsforskejlljustering i millisekunder. Alltid større eller lik 0.
     * @throws OperationalException hvis tidsforskjellen mellom appserver og databaseserver er størren enn {@link #MAX_TIME_DIFF_THRESHOLD}
     */
    private static synchronized long getDBMillisecDifference(Connection con) throws OperationalException {
        if (!dbMilliescDifferenceInitialized) {
            Timestamp dbTimestamp = getDBSystime(con);
            long dbTime = dbTimestamp.getTime();
            long serverTime = System.currentTimeMillis();
            Timestamp serverTimestamp = new Timestamp(serverTime);
            dbMillisecDifference = dbTime - serverTime;

            if (Math.abs(dbMillisecDifference) > MAX_TIME_DIFF_THRESHOLD) {
                String msg = "For stor tidsforskjell mellom database og server:" + dbMillisecDifference + " millisekunder. Database=" + dbTimestamp + " Server=" + serverTimestamp;
                logger.error(msg);
                if (dbMillisecDifference > 0) {
                    throw new OperationalException(msg);
                }
            }
            // Hvis database klokken er mindre enn server klokken, betyder det at låsen får lengre levetid hvilket ikke er noe problem.
            if (dbMillisecDifference < 0) {
                dbMillisecDifference = 0;
            }
            dbMilliescDifferenceInitialized = true;
        }
        return dbMillisecDifference;
    }

    /**
     * Henter systemtid fra databasen
     *
     * @param con databaseforbindelse
     * @return databasens systemtid
     */
    private static Timestamp getDBSystime(Connection con) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT SYSTIMESTAMP FROM DUAL")) {
            logger.debug("SQL: SELECT SYSTIMESTAMP FROM DUAL)");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getTimestamp(1);
        } catch (SQLException e) {
            throw new OperationalException("Error reading SYSTIMESTAMP from database", e);
        }
    }

    /**
     * Forsøker å opprette en ny lås uten å ta hensyn til om lås finnes fra før. Returner lås med låseinformasjon
     * hvis lås ble opprettet. Returnerer null hvis låsen ikke ble opprettet.
     *
     * @param con     databaseforbindelse
     * @param lockKey angi lockKey'en som skal låses
     * @param owner   nøkkel som brukes for låsing (brukerid)
     * @param expires utløpstidspunkt
     * @return låsen som ble opprettet, eller {@code null} hvis låsen ikke kunne opprettes
     */
    protected LockInfo<Long> insertLock(Connection con, LockKey<Long> lockKey, String owner, Timestamp expires) {
        String sql = "INSERT INTO " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " (ID, CLASS, OWNER, EXPIRES) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, lockKey.keyValue);
            stmt.setString(2, lockKey.discriminator);
            stmt.setString(3, owner);
            stmt.setTimestamp(4, expires);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL: INSERT INTO " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " (ID, CLASS, OWNER, EXPIRES) VALUES (?,?,?,?)");
                logger.debug("SQL: PARAM 1=" + lockKey.keyValue);
                logger.debug("SQL: PARAM 2=" + lockKey.discriminator);
                logger.debug("SQL: PARAM 3=" + owner);
                logger.debug("SQL: PARAM 4=" + expires);
            }
            stmt.executeUpdate();
            return new LockInfo<>(new LockKey<>(lockKey.discriminator, lockKey.keyValue), owner, expires, true);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                return null;
            } else {
                throw new OperationalException("Unexpected error locking lockKey: " + lockKey, e);
            }
        }
    }

    /**
     * Forsøker å opprette en haug med låser uten å ta hensyn til om lås finnes fra før. Returner låser med
     * låseinformasjon hvis alle låser ble opprettet. Returnerer null hvis noen låser feilet.
     * false.
     *
     * @param con      databaseforbindelse
     * @param lockKeys angi lockKey-ene som skal låses
     * @param owner    nøkkel som brukes for låsing (brukerid)
     * @param expires  utløpstidspunkt
     * @return låsene som ble opprettet, eller {@code null} hvis noen av låsene ikke kunne opprettes
     */
    protected Set<LockInfo<Long>> insertLocks(Connection con, Collection<LockKey<Long>> lockKeys, String owner, Timestamp expires) {
        Set<LockInfo<Long>> newLockInfos = new HashSet<>();
        String sqlString = "INSERT INTO " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " (ID, CLASS, OWNER, EXPIRES) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            for (LockKey<Long> lockKey : lockKeys) {
                stmt.setLong(1, lockKey.keyValue);
                stmt.setString(2, lockKey.discriminator);
                stmt.setString(3, owner);
                stmt.setTimestamp(4, expires);
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL: INSERT INTO " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " (ID, CLASS, OWNER, EXPIRES) VALUES (?,?,?,?)");
                    logger.debug("SQL: PARAM 1=" + lockKey.keyValue);
                    logger.debug("SQL: PARAM 2=" + lockKey.discriminator);
                    logger.debug("SQL: PARAM 3=" + owner);
                    logger.debug("SQL: PARAM 4=" + expires);
                }
                stmt.addBatch();
                newLockInfos.add(new LockInfo<>(lockKey, owner, expires, true));
            }
            stmt.executeBatch();
            return newLockInfos;
        } catch (BatchUpdateException e) {
            // Fikk ikke lov å opprette alle låsene
            try {
                con.rollback();
                return null;
            } catch (SQLException e1) {
                throw new OperationalException(e1);
            }
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                e.addSuppressed(e1);
            }
            throw new OperationalException("Locking ids failed: " + lockKeys, e);
        }
    }


    /**
     * Henter opp lås for gitt lockKey.
     *
     * @param con     connection
     * @param lockKey key som skal låses
     * @return lås eller null hvis lås ikke lengre finnes
     */
    private LockInfo<Long> getLock(Connection con, LockKey<Long> lockKey) {
                String sqlString = "SELECT OWNER, EXPIRES FROM " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " WHERE ID=? AND CLASS=?";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            stmt.setLong(1, lockKey.keyValue);
            stmt.setString(2, lockKey.discriminator);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL: " + sqlString);
                logger.debug("SQL: PARAM 1=" + lockKey.keyValue);
                logger.debug("SQL: PARAM 2=" + lockKey.discriminator);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String lockedBykey = rs.getString(1);
                Timestamp expires = rs.getTimestamp(2);
                return new LockInfo<>(lockKey, lockedBykey, expires, false);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new OperationalException("Search for locks with lockKey failed: " + lockKey, e);
        }
    }

    /**
     * Fornyer eksisterende lås med nytt utløpstidspunkt
     *
     * @param con      databaseforbindelse
     * @param lockInfo lås som skal fornyes
     * @param expires  nytt utløpstidspunkt
     * @return lås med fornyet utløpstidspunkt
     * @throws LockedException hvis låsen ikke kunne fornyes
     */
    private LockInfo<Long> renewLock(Connection con, LockInfo<Long> lockInfo, Timestamp expires) throws LockedException {
        String sql = "UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET EXPIRES=? WHERE ID=? AND CLASS=? AND OWNER=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, expires);
            stmt.setLong(2, lockInfo.getLockKey().keyValue);
            stmt.setString(3, lockInfo.getLockKey().discriminator);
            stmt.setString(4, lockInfo.getOwner());
            if (logger.isDebugEnabled()) {
                logger.debug("SQL: UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET EXPIRES=? WHERE ID=? AND CLASS=? AND OWNER=?");
                logger.debug("SQL: PARAM 1=" + expires);
                logger.debug("SQL: PARAM 2=" + lockInfo.getLockKey().keyValue);
                logger.debug("SQL: PARAM 3=" + lockInfo.getLockKey().discriminator);
                logger.debug("SQL: PARAM 4=" + lockInfo.getOwner());
            }
            int result = stmt.executeUpdate();
            if (result == 0) {
                LockInfo<Long> newLockInfo = getLock(con, lockInfo.getLockKey());
                if (newLockInfo == null) {
                    newLockInfo = new LockInfo<>(lockInfo.getLockKey(), "", new Timestamp(System.currentTimeMillis()), false);
                }
                throw new LockedException(lockInfo.getOwner(), newLockInfo);
            }
            return new LockInfo<>(lockInfo.getLockKey(), lockInfo.getOwner(), expires, false);
        } catch (SQLException e) {
            throw new OperationalException("Unexpected error locking id: " + lockInfo.getLockKey(), e);
        }
    }

    /**
     * Overtar eierskap av lås fra annen bruker og setter nytt utløpstidspunkt. Verifiserer at låsen virkelig er
     * utløpt.
     *
     * @param con      databaseforbindelse
     * @param owner    nøkkel som brukes for låsing (brukerid)
     * @param expires  utløpstidspunkt
     * @param lockInfo lås som skal opprettes
     * @return ny lås
     */
    private LockInfo<Long> timeoutAndTakeLock(Connection con, String owner, Timestamp expires, LockInfo<Long> lockInfo) {
        String sql = "UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET OWNER=?, EXPIRES=? WHERE ID=? AND CLASS=? AND EXPIRES<SYSTIMESTAMP";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, owner);
            stmt.setTimestamp(2, expires);
            stmt.setLong(3, lockInfo.getLockKey().keyValue);
            stmt.setString(4, lockInfo.getLockKey().discriminator);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL: UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET OWNER=?, EXPIRES=? WHERE ID=? AND CLASS=? AND EXPIRES< SYSTIMESTAMP");
                logger.debug("SQL: PARAM 1=" + owner);
                logger.debug("SQL: PARAM 2=" + lockInfo.getLockKey().keyValue);
                logger.debug("SQL: PARAM 3=" + expires);
                logger.debug("SQL: PARAM 4=" + lockInfo.getLockKey().discriminator);
            }
            int result = stmt.executeUpdate();
            if (result == 0) {
                throw new LockedException(owner, lockInfo);
            }
            return new LockInfo<>(lockInfo.getLockKey(), lockInfo.getOwner(), expires, true);
        } catch (SQLException e) {
            throw new OperationalException("Error locking id: " + lockInfo.getLockKey(), e);
        }
    }

    /**
     * Finner eksisterende lock for lockKeys.
     *
     * @param con      Connection
     * @param lockKeys lockKeys å finne låser for
     * @return map av eksisterende locks (key=BubbleId, value=DBLock)
     */
    private Set<LockInfo<Long>> findExistingLocks(Connection con, Set<LockKey<Long>> lockKeys) {
        Set<LockInfo<Long>> lockInfos = new HashSet<>();
        Iterator<LockKey<Long>> keyIteratory = lockKeys.iterator();
        while (keyIteratory.hasNext()) {
            findLocksForBatch(con, lockInfos, keyIteratory);
        }
        return lockInfos;
    }

    /**
     * Gjør et søk i databasen etter låse med bestemt id'er. Det søkes etter 10 id'er per kald.
     *
     * @param con         Connection
     * @param lockInfos   Collection låser skal puttes inn i
     * @param keyIterator Iterator for keys det skal søkes for
     */
    private void findLocksForBatch(Connection con, Set<LockInfo<Long>> lockInfos, Iterator<LockKey<Long>> keyIterator) {
        Set<LockKey<Long>> idsWanted = new HashSet<>();
        // Vi spør ikke etter CLASS da det komplisere query. Gjør en filter etter på
        String sqlString = "SELECT ID, CLASS, OWNER, EXPIRES FROM " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " WHERE ID IN (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            for (int i = 1; i <= 10; i++) {
                if (keyIterator.hasNext()) {
                    LockKey<Long> lockKey = keyIterator.next();
                    idsWanted.add(lockKey);
                    stmt.setLong(i, lockKey.keyValue);
                } else {
                    stmt.setNull(i, java.sql.Types.NUMERIC);
                }
            }
            logger.debug("SQL: " + sqlString);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long idValue = rs.getLong(1);
                String baseIdClassName = rs.getString(2);
                String key = rs.getString(3);
                Timestamp timestamp = rs.getTimestamp(4);
                LockKey<Long> lockKey = new LockKey<>(baseIdClassName, idValue);
                if (idsWanted.contains(lockKey)) {
                    lockInfos.add(new LockInfo<>(lockKey, key, timestamp, false));
                }
            }
        } catch (SQLException e) {
            throw new OperationalException("Reading of locks failed", e);
        }
    }

    /**
     * Hjelpemetode som returnerer låse i locks som ikke eies av key
     *
     * @param locks LockInfos vi skal sjekke eier for
     * @param owner Owner vi skal søke for
     * @return låse i locks som ikke eies av key
     */
    private Set<LockInfo<Long>> getLocksNotOwnedByKey(Set<LockInfo<Long>> locks, String owner) {
        Set<LockInfo<Long>> notOwnedByKey = new HashSet<>();
        for (LockInfo<Long> lock : locks) {
            if (!lock.isOwnedBy(owner)) {
                notOwnedByKey.add(lock);
            }
        }
        return notOwnedByKey;
    }

    private void verifyAllLocksExpired(Set<LockInfo<Long>> locksNotOwnedByKey, String owner) throws LockedException {
        Set<LockInfo<Long>> notExpired = new HashSet<>();
        for (LockInfo<Long> lock : locksNotOwnedByKey) {
            if (!lock.expired()) {
                notExpired.add(lock);
            }
        }
        if (!notExpired.isEmpty()) {
            logger.debug("Cannot lock objects for user " + owner + ": " + notExpired);
            throw new LockedException(owner, notExpired);
        }
    }

    /**
     * Hjelpemetod som returnerer de id'er i ids som ikke har en tilhørende lås i locks.
     *
     * @param ids   Set av id'er som skal brukes som i utgangspunkt i filtreringen
     * @param locks låse hvis id'er skal fjernes
     * @return set av id'er som ikke har en tilhørende lås i locks
     */
    private Set<LockKey<Long>> getIdsWithNoLock(Set<LockKey<Long>> ids, Set<LockInfo<Long>> locks) {
        Set<LockKey<Long>> result = new HashSet<>(ids);
        for (LockInfo<Long> lock : locks) {
            result.remove(lock.getLockKey());
        }
        return result;
    }

    /**
     * Forlenger låsetiden på spesifiserte låser for key
     *
     * @param con           databaseforbindelse
     * @param existingLocks låser som skal fornyes
     * @param expires       nytt utløpstidspunkt
     * @param owner         nøkkel som brukes for låsing (brukerid)
     * @return fornyet låser
     * @throws LockedException dersom ikke alle låser kunne fornyes (f.eks noen av låsene var timet ut)
     */
    private Set<LockInfo<Long>> renewLocks(Connection con, Set<LockInfo<Long>> existingLocks, Timestamp expires, String owner) throws LockedException {
        try {
            return renewLocksUsingQracleBatching(con, existingLocks, expires, owner);
        } catch (OracleBatchUpdateCountException e) {
            Collection<LockInfo<Long>> locks = findLocksNotOwnedByKey(con, owner, existingLocks);
            throw new LockedException(owner, locks);
        }
    }

    private Set<LockInfo<Long>> renewLocksUsingQracleBatching(Connection con, Set<LockInfo<Long>> existingLocks, Timestamp expires, String owner) throws OracleBatchUpdateCountException {
        if (existingLocks.size() == 0) return new HashSet<>();
        int batchResult = 0;
        Set<LockInfo<Long>> renewedLocks = new HashSet<>(existingLocks.size());
        String sqlString = "UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET EXPIRES=? WHERE ID=? AND CLASS=? AND OWNER=?";
        OracleConnection oracleCon = OracleUtils.getOracleConnection(con);
        try (OraclePreparedStatement ps = (OraclePreparedStatement) oracleCon.prepareStatement(sqlString)) {
            ps.setExecuteBatch(Math.min(existingLocks.size(), MAX_BATCH_SIZE));
            for (LockInfo<Long> lockInfo : existingLocks) {
                ps.setTimestamp(1, expires);
                ps.setLong(2, lockInfo.getLockKey().keyValue);
                ps.setString(3, lockInfo.getLockKey().discriminator);
                ps.setString(4, lockInfo.getOwner());
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL: " + sqlString);
                    logger.debug("SQL: PARAM 1=" + expires);
                    logger.debug("SQL: PARAM 2=" + lockInfo.getLockKey().keyValue);
                    logger.debug("SQL: PARAM 3=" + lockInfo.getLockKey().discriminator);
                    logger.debug("SQL: PARAM 4=" + lockInfo.getOwner());
                }
                batchResult += ps.executeUpdate();
                renewedLocks.add(new LockInfo<>(lockInfo.getLockKey(), owner, expires, false));
            }
            batchResult += ps.sendBatch();
            if (batchResult != existingLocks.size()) {
                throw new OracleBatchUpdateCountException(batchResult, existingLocks.size());
            }
            return renewedLocks;
        } catch (SQLException e) {
            throw new OperationalException("Error locking objects", e);
        }
    }

    private Set<LockInfo<Long>> findLocksNotOwnedByKey(Connection con, String owner, Set<LockInfo<Long>> locks) {
        Set<LockKey<Long>> lockKeys = new HashSet<>(locks.size());
        for (LockInfo<Long> lock : locks) {
            lockKeys.add(lock.getLockKey());
        }
        Set<LockInfo<Long>> existingLocks = findExistingLocks(con, lockKeys);
        addMissingLocks(lockKeys, existingLocks);
        return getLocksNotOwnedByKey(existingLocks, owner);
    }

    /**
     * Legger inn fiktive låser for de ide'er som ikke allerede har en lås i existingLocks slik at existingLocks får en lås
     * for hver id i ids. Denne metode brukes ifm feilrapportering hvor det i teorien kan oppstå en race condition hvor
     * en bruker ikke får tatt eller fornyet en lås men ikke finner låsen i databasen under feilrapportering fordi
     * låsen i mellomtiden den har blitt slettet av brukeren som eide den.
     *
     * @param lockKeys      id'er som må ha en lås
     * @param existingLocks eksisterende låser.
     */
    private void addMissingLocks(Set<LockKey<Long>> lockKeys, Set<LockInfo<Long>> existingLocks) {
        HashSet<LockKey<Long>> lockKeys2 = new HashSet<>(lockKeys);
        for (LockInfo<Long> existingLock : existingLocks) {
            lockKeys2.remove(existingLock.getLockKey());
        }
        for (LockKey<Long> lockKey : lockKeys2) {
            existingLocks.add(new LockInfo<>(new LockKey<>(lockKey.discriminator, lockKey.keyValue), "", new Timestamp(System.currentTimeMillis()), false));
        }
    }

    private Set<LockInfo<Long>> timeoutAndTakeLocks(Connection con, Set<LockInfo<Long>> locksToTimeout, String owner, Timestamp expires) {
        try {
            return timeoutAndTakeLocksUsingQracleBatching(con, locksToTimeout, owner, expires);
        } catch (OracleBatchUpdateCountException e) {

            Collection<LockInfo<Long>> locks = findLocksNotOwnedByKey(con, owner, locksToTimeout);
            throw new LockedException(owner, locks);
        }
    }

    private Set<LockInfo<Long>> timeoutAndTakeLocksUsingQracleBatching(Connection con, Set<LockInfo<Long>> locksToTimeout, String owner, Timestamp expires) throws OracleBatchUpdateCountException {
        if (locksToTimeout.size() == 0) return new HashSet<>();
        int batchResult = 0;
        List<LockInfo<Long>> locksToTimeoutList = new ArrayList<>(locksToTimeout);
        Set<LockInfo<Long>> timedoutLocks = new HashSet<>(locksToTimeout.size());
        String sqlString = "UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET OWNER=?, EXPIRES=? WHERE ID=? AND CLASS=? AND EXPIRES < SYSTIMESTAMP";
        OracleConnection oracleCon = OracleUtils.getOracleConnection(con);
        try (OraclePreparedStatement ps = (OraclePreparedStatement) oracleCon.prepareStatement(sqlString)) {
            ps.setExecuteBatch(Math.min(locksToTimeoutList.size(), MAX_BATCH_SIZE));
            for (LockInfo<Long> lock : locksToTimeoutList) {
                ps.setString(1, owner);
                ps.setTimestamp(2, expires);
                ps.setLong(3, lock.getLockKey().keyValue);
                ps.setString(4, lock.getLockKey().discriminator);
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL: " + sqlString);
                    logger.debug("SQL: PARAM 1=" + owner);
                    logger.debug("SQL: PARAM 2=" + expires);
                    logger.debug("SQL: PARAM 3=" + lock.getLockKey().keyValue);
                    logger.debug("SQL: PARAM 4=" + lock.getLockKey().discriminator);
                }
                batchResult += ps.executeUpdate();
                timedoutLocks.add(new LockInfo<>(lock.getLockKey(), owner, expires, true));
            }

            batchResult += ps.sendBatch();
            if (batchResult != locksToTimeoutList.size()) {
                throw new OracleBatchUpdateCountException(batchResult, locksToTimeoutList.size());
            }
            return timedoutLocks;
        } catch (SQLException e) {
            throw new OperationalException("Error locking objects", e);
        }
    }

    private void unlockAll(Connection con, Set<LockKey<Long>> lockKeys, String owner) {
        Iterator<LockKey<Long>> idIterator = lockKeys.iterator();
        while (idIterator.hasNext()) {
            unlockBatch(con, owner, idIterator);
        }
    }

    private void unlockBatch(Connection con, String owner, Iterator<LockKey<Long>> idIterator) {
        int BATCH_SIZE = 10;

        StringBuilder buf = new StringBuilder();
        buf.append("DELETE FROM ").append(configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME)).append(" WHERE (ID=? AND CLASS=? AND OWNER=?) ");
        for (int i = 2; i <= BATCH_SIZE; i++) {
            buf.append("OR (ID=? AND CLASS=? AND OWNER=?)");
        }

        try (PreparedStatement stmt = con.prepareStatement(buf.toString())) {
            for (int i = 1; i <= BATCH_SIZE * 3; i = i + 3) {
                if (idIterator.hasNext()) {
                    LockKey<Long> id = idIterator.next();
                    stmt.setLong(i, id.keyValue);
                    stmt.setString(i + 1, id.discriminator);
                    stmt.setString(i + 2, owner);
                } else {
                    stmt.setNull(i, java.sql.Types.NUMERIC);
                    stmt.setNull(i + 1, java.sql.Types.VARCHAR);
                    stmt.setNull(i + 2, java.sql.Types.VARCHAR);
                }
            }
            logger.debug(buf.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new OperationalException("Error deleting locks", e);
        }

    }

    private Collection<LockInfo<Long>> getLocksBy(Connection con, String owner) {
        Collection<LockInfo<Long>> locks = new ArrayList<>();
        String sqlString = "SELECT ID, CLASS, EXPIRES FROM " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " WHERE OWNER=?";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            stmt.setString(1, owner);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL: " + sqlString);
                logger.debug("SQL: PARAM 1=" + owner);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long keyValue = rs.getLong(1);
                String discriminator = rs.getString(2);
                Timestamp expires = rs.getTimestamp(3);
                locks.add(new LockInfo<>(new LockKey<>(discriminator, keyValue), owner, expires, false));
            }
            return locks;
        } catch (SQLException e) {
            throw new OperationalException("Search for locks for user failed: " + owner, e);
        }
    }

    private void renewAllLocks(Connection con, String owner, Timestamp expires) {
        // NB: Records som har en lengre utløpstid enn den nye utløpstid oppdateres ikke.
        String sqlString = "UPDATE " + configuration.getString(SkifConfigConstants.DB_LOCK_TABLENAME) + " SET EXPIRES=? WHERE OWNER=? AND EXPIRES < ?";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            stmt.setTimestamp(1, expires);
            stmt.setString(2, owner);
            stmt.setTimestamp(3, expires);
            logger.debug("SQL: " + sqlString);
            logger.debug("SQL: PARAM 1=" + expires);
            logger.debug("SQL: PARAM 2=" + owner);
            logger.debug("SQL: PARAM 3=" + expires);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new OperationalException("Modifying lock expiration time for user failed: " + owner, e);
        }

    }


}
