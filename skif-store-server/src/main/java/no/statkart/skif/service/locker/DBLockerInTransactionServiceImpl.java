package no.statkart.skif.service.locker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.OperationalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBLockerInTransactionServiceImpl implements DBLockerInTransactionService<Long> {
    private static final Logger logger = LoggerFactory.getLogger(DBLockerInTransactionServiceImpl.class);

    final Provider<Connection> connectionProvider;

    @Inject
    public DBLockerInTransactionServiceImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public int consumeAllLocks(String owner) {
        Connection con = connectionProvider.get();
        String sqlString = "DELETE FROM LOCKINFO WHERE OWNER=?";
        try (PreparedStatement stmt = con.prepareStatement(sqlString)) {
            stmt.setString(1, owner);
            logger.debug("SQL: " + sqlString);
            logger.debug("SQL: PARAM 1=" + owner);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new OperationalException("Deleting all locks for user failed: " + owner, e);
        }

    }
}