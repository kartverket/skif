package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;

import java.util.*;

/**
 * Implementasjon av LockerStrategy som fungerer for BubbleIds som har en Long som value. Holder på alle
 * låser
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class TransactionalLockerStrategy implements LockerStrategy {

    //Brukes for å holde rede på låser tatt i transaksjonen, samt de som allerede finnes for brukere i transaksjon. Initialiseres som null for å kunne kjøre populate senere.
    private Map<BubbleId, LockInfo<Long>> lockMap = null;

    //Brukes for å holde rede på hvilke ids som er nye og som derfor ikke kan låses opp.
    private final Set<BubbleId> insertedIds = new HashSet<BubbleId>();

    //Brukes for å holde rede på hvilke ids som er endret og som derfor ikke kan låses opp.
    private final Set<BubbleId> modifiedIds = new HashSet<BubbleId>();

    //Brukes for å finne ut av hvilke låser som skal frigis etter fullføring av transaksjon.
    private final Set<BubbleId> newLockIds = new HashSet<BubbleId>();

    //Brukes for å holde rede på hvilke elementer man ønsker å låse opp, men som ikke er låst i denne transaksjonen.
    private final Set<BubbleId> unlockIds = new HashSet<BubbleId>();

    private final DBLockerService<Long> lockerService;
    private final DBLockerInTransactionService<Long> lockerInTransactionService;
    private final ServiceRequestContext serviceRequestContext;

    //TODO: Skal disse være her?
    private final long LOCK_TIMEOUT;
    private final long MAX_TRANSACTION_DURATION;

    //Informasjon om man har verifisert at låser for bruker vil vare til MAX_TRANSACTION_DURATION
    private boolean locksVerified = false;


    @Inject
    public TransactionalLockerStrategy(DBLockerService<Long> lockerService, DBLockerInTransactionService<Long> lockerInTransactionService, Configuration configuration, ServiceRequestContext serviceRequestContext) {
        this.lockerService = lockerService;
        this.lockerInTransactionService = lockerInTransactionService;
        this.serviceRequestContext = serviceRequestContext;

        LOCK_TIMEOUT = configuration.getLong(SkifConfigConstants.LOCK_TIMEOUT);
        MAX_TRANSACTION_DURATION = configuration.getLong(SkifConfigConstants.MAX_TRANSACTION_DURATION);
    }

    @Override
    public boolean lock(BubbleId id) {
        boolean lockIsNew;
        String owner = serviceRequestContext.getUserName();

        if (insertedIds.contains(id)) {
            lockIsNew = false;
        } else {
            ensureLockMapInitialized();
            LockInfo<Long> lock = lockMap.get(id);
            if (lock == null || !renewNotRequired(lock)) {
                lock = lockerService.lock(createLockKey(id), owner, LOCK_TIMEOUT);
                lockMap.put(id, lock);
                if (lock.isNew()) {
                    newLockIds.add(id);
                }
                lockIsNew = lock.isNew();
            } else {
                lockIsNew = false;
            }

        }

        return lockIsNew;
    }


    @Override
    public void unlock(BubbleId id) {
        String owner = serviceRequestContext.getUserName();
        if (insertedIds.contains(id)) {
            throw new ImplementationException("Forsøkte å låse opp objekt som er inserted: " + id.toString());
        } else if (modifiedIds.contains(id)) {
            throw new ImplementationException("Forsøkte å låse opp objekt som er endret: " + id.toString());
        } else if (newLockIds.remove(id)) {
            lockerService.unlock(createLockKey(id), owner);
            if (lockMap != null) {
                lockMap.remove(id);
            }
        } else {
            unlockIds.add(id);
        }
    }

    @Override
    public boolean isLockedByCaller(BubbleId id) {
        String owner = serviceRequestContext.getUserName();
        ensureLockMapInitialized();
        if (lockMap.containsKey(id)) {
            LockInfo<Long> lockInfo = lockMap.get(id);
            return lockInfo.isOwnedBy(owner);
        } else {
            return false;
        }
    }

    @Override
    public boolean isLockedByOther(BubbleId id) {
        String owner = serviceRequestContext.getUserName();
        LockInfo<Long> lock = lockerService.getLock(createLockKey(id));
        return lock != null && !lock.isOwnedBy(owner);
    }

    @Override
    public void releaseAllLocks() {
        String owner = serviceRequestContext.getUserName();
        ensureLockMapInitialized();
        Set<LockKey<Long>> idsForUnlock = new HashSet<LockKey<Long>>();
        for (Map.Entry<BubbleId, LockInfo<Long>> entry : lockMap.entrySet()) {
            if (entry.getValue().getOwner().equals(owner) && !modifiedIds.contains(entry.getKey()) && !insertedIds.contains(entry.getKey())) {
                if (newLockIds.contains(entry.getKey())) {
                    idsForUnlock.add(entry.getValue().getLockKey());
                } else {
                    unlockIds.add(entry.getKey()); //Element er ikke låst i denne transaksjonen og vil bli låst opp når denne er ferdig
                }
            }
        }

        lockerService.unlockAll(idsForUnlock, owner);
    }

    @Override
    public void releaseLocksOnRollback() {
        String owner = serviceRequestContext.getUserName();
        if (!newLockIds.isEmpty()) {
            lockerService.unlockAll(createLockKeys(newLockIds), owner);
        }
    }


    @Override
    public void clear() {
        lockMap = null;
        insertedIds.clear();
        modifiedIds.clear();
        newLockIds.clear();
        unlockIds.clear();
    }

    @Override
    public void registerInserted(BubbleId id) {
        insertedIds.add(id);
    }

    @Override
    public void registerUpdated(BubbleId id) {
        if (!insertedIds.contains(id)) {
            ensureLockedByCaller(id);
            modifiedIds.add(id);
        }
    }

    @Override
    public void registerRemoved(BubbleId id) {
        if (!insertedIds.contains(id)) {
            ensureLockedByCaller(id);
            modifiedIds.add(id);
        }
    }

    @Override
    public void consumeAllLocks() {
        String owner = serviceRequestContext.getUserName();
        ensureLockMapInitialized();
        lockerInTransactionService.consumeAllLocks(owner, lockMap.size());
    }

    @Override
    public void releaseLocksOnNonTransactionalScopeCompletion() {
        String owner = serviceRequestContext.getUserName();
        if (!unlockIds.isEmpty()) {
            lockerService.unlockAll(createLockKeys(unlockIds), owner);
        }
    }

    /**
     * Finner ut om en eksisterende lås trenger å bli fornyet
     *
     * @param lock LockInfo<Long> som skal sjekkes
     * @return true dersom låsen ikke trenger å fornyes
     */
    private boolean renewNotRequired(LockInfo<Long> lock) {
        return !lock.expiresBefore(MAX_TRANSACTION_DURATION);
    }

    /**
     * Oppretter en instans av klassen definert av discriminator for lockKey
     *
     * @param lockKey LockKey som gir verdier for instansen
     * @return BubbleId av type angitt av lockKey sin discriminator
     */
    private BubbleId createBubbleIdFromLockKey(LockKey<Long> lockKey) {
        try {
            Class<? extends BubbleId> idClass = Class.forName(lockKey.discriminator).asSubclass(BubbleId.class);
            return BubbleIds.createInstance(idClass, lockKey.keyValue, SnapshotVersion.CURRENT);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("Class.forName feilet for klassen " + lockKey.discriminator + " i TransactionalLockerStrategy", e);
        } catch (ClassCastException e) {
            throw new ImplementationException("Class.forName returnerte ikke-bobleid-klasse for " + lockKey.discriminator + " i TransactionalLockerStrategy", e);
        }
    }

    private LockKey<Long> createLockKey(BubbleId id) {
        if (id.getValue() instanceof Long) {
            return new LockKey<Long>(id.getClass().getName(), (Long) id.getValue());
        } else {
            return null;
        }
    }

    private Set<LockKey<Long>> createLockKeys(Set<BubbleId> ids) {
        Set<LockKey<Long>> lockKeys = new HashSet<LockKey<Long>>();
        for (BubbleId id : ids) {
            lockKeys.add(createLockKey(id));
        }
        return lockKeys;
    }

    private void ensureLockMapInitialized() {
        if (lockMap == null) {
            lockMap = new HashMap<BubbleId, LockInfo<Long>>();
            initializeLockMap();
        } else if (lockMapInitializedForDifferentOwner()) {
            initializeLockMap();
        }
    }

    /**
     * Sjekker om låser i lockMap tilhører owner
     *
     * @return true dersom ingen av låsene i LockMap tilhører owner
     */
    private boolean lockMapInitializedForDifferentOwner() {
        String owner = serviceRequestContext.getUserName();
        for (Map.Entry<BubbleId, LockInfo<Long>> entry : lockMap.entrySet()) {
            if (entry.getValue().isOwnedBy(owner)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Henter låser for owner fra DBLockerService og legger disse i lockMap
     */
    private void initializeLockMap() {
        String owner = serviceRequestContext.getUserName();
        Collection<LockInfo<Long>> locksForOwner = lockerService.getLocksBy(owner);
        for (LockInfo<Long> lock : locksForOwner) {
            lockMap.put(createBubbleIdFromLockKey(lock.getLockKey()), lock);
        }
    }

    /**
     * Verifies that the specified id is already locked by caller.
     *
     * @param id    Id som skal sjekkes
     * @throws no.statkart.skif.exception.NotLockedException
     *          dersom brukeren ikke har noen lås på id-en
     */
    protected synchronized void ensureLockedByCaller(BubbleId id) throws NotLockedException {
        ensureLockMapInitialized();
        String owner = serviceRequestContext.getUserName();
        LockInfo<Long> lock = lockMap.get(id);
        if (lock == null) {
            throw new NotLockedException("BubbleId: " + id + " not locked by " + owner);
        }

        // We only need to verify once per service call, because we assume that the LOCK_TIMEOUT created for new locks
        // during the service call always will be longer that MAX_TRANSACTION_DURATION.
        if (!locksVerified) {
            if (lock.expiresBefore(MAX_TRANSACTION_DURATION)) {
                renewAllLocks(owner);
                locksVerified = true;
            }
        }
    }

    /**
     * @param owner Bruker som skal få alle sine låser fornyet
     */
    private void renewAllLocks(String owner) {
        lockerService.renewAllLocks(owner, LOCK_TIMEOUT);
        initializeLockMap();
    }
}
