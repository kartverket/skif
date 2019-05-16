package no.statkart.skif.store;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.type.TimestampType;

import java.sql.*;

/**
 * @since 2.1
 */
public class SnapshotVersionSessionHelper {
    // TODO: Virker ikke hvis Hibernate.TIMESTAMP brukes. Deprecated i 3.6.10
    private static TimestampType TIMESTAMP = new TimestampType();

    /**
     * Henter SnapshotVersion satt på databasen
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
     * Henter transaksjonstidspunktet i databasen.
     *
     * @param connection gjeldende databasesesjon
     * @return transaksjonstidspunkt
     */
    public static Timestamp getTransactionTime(Connection connection) {
        CallableStatement statement = null;
        try {
            statement = connection.prepareCall("{? = call snapshot_time.Get_T_Trans() }");
            statement.registerOutParameter(1, Types.TIMESTAMP);
            statement.executeUpdate();
            return statement.getTimestamp(1);
        } catch (SQLException e) {
            throw new OperationalException(e);
        } finally {
            JDBCHelper.close(statement);
        }
    }
}
