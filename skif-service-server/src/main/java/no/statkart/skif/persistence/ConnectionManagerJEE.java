package no.statkart.skif.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Henrik Fredholm
 */
public class ConnectionManagerJEE implements ConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManagerJEE.class);
    private final ConnectionFactory facotry;
    private final ServiceRequestContext serviceRequestContext;
    private Connection connection;
    private boolean originalAutoCommit;
    private boolean autoCommit;


    @Inject
    public ConnectionManagerJEE(ConnectionFactory facotry, ServiceRequestContext serviceRequestContext) {
        this.facotry = facotry;
        this.serviceRequestContext = serviceRequestContext;
    }

    public boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
//        if (connection!= null) {
//            connection.setAutoCommit(autoCommit);
//        }
    }



    protected void openConnection() throws SQLException {
        logger.debug("Open Connection");
        connection = facotry.createConnection();
        originalAutoCommit = connection.getAutoCommit();
        if (originalAutoCommit !=autoCommit) {
            throw new ImplementationException("Mismatch in expected autocommit mode. Expected: " + autoCommit + " Found: " + originalAutoCommit);
        }
    }

    protected void closeConnection() throws SQLException {
        connection.setAutoCommit(originalAutoCommit);
        connection.close();
        connection = null;
        logger.debug("Close Connection");
    }

    @Override
    public Connection getConnection(Object key) throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    @Override
    public boolean isActive(Object key) {
        return connection != null;
    }

    @Override
    public void close(Object key) throws SQLException {
        if (connection != null) {
            closeConnection();
        }
    }

    @Override
    public void close() throws SQLException {
        close(null);
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }
}
