package no.statkart.skif.store;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.OperationalException;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @since 2.1
 */
public class SnapshotVersionSessionHelper {
    /**
     * Henter SnapshotVersion satt på databasen
     */
    public static SnapshotVersion getSnapshotVersion(Session session) {
        try {
            final NativeQuery<?> sqlQuery = session.createNativeQuery("select snapshot_time.get_t() as t from dual");
            sqlQuery.addScalar("t", StandardBasicTypes.TIMESTAMP);
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
        try (CallableStatement statement = connection.prepareCall("{? = call snapshot_time.Get_T_Trans() }")) {
            statement.registerOutParameter(1, Types.TIMESTAMP);
            statement.executeUpdate();
            return statement.getTimestamp(1);
        } catch (SQLException e) {
            throw new OperationalException(e);
        }
    }
}
