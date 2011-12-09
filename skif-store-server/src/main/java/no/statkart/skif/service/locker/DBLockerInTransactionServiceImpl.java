package no.statkart.skif.service.locker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.util.JDBCHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBLockerInTransactionServiceImpl implements DBLockerInTransactionService<Long> {
    private static Logger logger = LoggerFactory.getLogger(DBLockerInTransactionServiceImpl.class);

    final Provider<Connection> connectionProvider;

    @Inject
    public DBLockerInTransactionServiceImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void consumeAllLocks(String owner, final int expectedLockCount) {
        Connection con = connectionProvider.get();
        PreparedStatement stmt = null;
        try {
            String sqlString = "DELETE FROM LOCKINFO WHERE OWNER=?";
            stmt = con.prepareStatement(sqlString);
            stmt.setString(1, owner);
            logger.debug("SQL: " + sqlString);
            logger.debug("SQL: PARAM 1=" + owner);
            final int consumedLockCount = stmt.executeUpdate();

            if (consumedLockCount != expectedLockCount) {
                throw new OperationalException("Låsene ble borte under fullføring av brukstilfellet. Brukstilfellet har sannsynligvis blitt fullført på en annen tjener.");
            }
        } catch (SQLException e) {
            throw new OperationalException("Sletting av alle låser for bruker feilet: " + owner, e);
        } finally {
            JDBCHelper.close(stmt);
        }

    }
}