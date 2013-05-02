package no.statkart.skif.store;

import no.statkart.skif.exception.ConfigurationException;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.type.TimestampType;

import java.sql.Timestamp;

import static no.statkart.skif.store.SnapshotVersionHelper.calcJustBeforeOf;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SnapshotVersionSessionHelper {
    // TODO: Virker ikke hvis Hibernate.TIMESTAMP brukes. Deprecated i 3.6.10
    private static TimestampType TIMESTAMP = new TimestampType();

    public static void setSnapshotToJustBefore(Session session, SnapshotVersion snapshotVersion) {
        final Timestamp timestamp = calcJustBeforeOf(snapshotVersion.getTimestamp());
        session.createSQLQuery("select snapshot_time.set_t(:timestamp) from dual").setTimestamp("timestamp", timestamp).executeUpdate();
    }

    /**
     * Setter SnapshotVersion på databasen.
     * <p/>
     * Har package scope for testing
     */
    public static void setSnapshotVersion(Session session, SnapshotVersion snapshotVersion) {
        try {
            session.clear();
            final SQLQuery sqlQuery = session.createSQLQuery("select snapshot_time.set_t(:timestamp) from dual");
            sqlQuery.setTimestamp("timestamp", snapshotVersion.getTimestamp()).executeUpdate();
        } catch (SQLGrammarException e) {
            if (e.getErrorCode() == 904 && e.getSQLState().equals("42000") && e.getCause().getMessage().equals("ORA-00904: \"SNAPSHOT_TIME\".\"SET_T\": ugyldig identifikator\n")) {
                throw new ConfigurationException("Database schema does not support history");
            } else {
                throw e;
            }
        }
    }

    /**
     * Henter SnapshotVersion svarende til 'now' fra databasen
     *
     * @param session
     */
    public static SnapshotVersion getSnapshotVersionNow(Session session) {
        final SQLQuery sqlQuery = session.createSQLQuery("select systimestamp as t from dual");
        sqlQuery.addScalar("t", TIMESTAMP);

        return SnapshotVersion.createInstance((Timestamp) sqlQuery.uniqueResult());
    }

    /**
     * Henter SnapshotVersion satt på datbasen
     *
     * @param session
     * @return
     */
    public static SnapshotVersion getSnapshotVersion(Session session) {
        try {
            final SQLQuery sqlQuery = session.createSQLQuery("select snapshot_time.get_t() as t from dual");
            sqlQuery.addScalar("t", TIMESTAMP);
            return SnapshotVersion.createInstance((Timestamp) sqlQuery.uniqueResult());
        } catch (SQLGrammarException e) {
            if (e.getErrorCode() == 904 && e.getSQLState().equals("42000") && e.getCause().getMessage().equals("ORA-00904: \"SNAPSHOT_TIME\".\"GET_T\": ugyldig identifikator\n")) {
                throw new ConfigurationException("Database schema does not support history");
            } else {
                throw e;
            }
        }
    }

    /**
     * Nullstiller transaksjonstidspunkt slik at etterfølgende endringer kommer i egne historisk transaksjon
     *
     * @param session
     */
    public static void clearTransactionSnapshotTime(Session session) {
        final SQLQuery sqlQuery = session.createSQLQuery("delete from snapshot_trans");
        sqlQuery.executeUpdate();
    }


}
