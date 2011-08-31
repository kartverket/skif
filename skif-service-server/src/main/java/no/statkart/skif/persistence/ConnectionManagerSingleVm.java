package no.statkart.skif.persistence;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class ConnectionManagerSingleVm implements ConnectionManager {
    private final ConnectionFactory facotry;
    private final ServiceRequestContext serviceRequestContext;
    private Connection connection;
    private boolean autoCommit = false;
    private boolean originalAutoCommit;


    @Inject
    public ConnectionManagerSingleVm(ConnectionFactory facotry, ServiceRequestContext serviceRequestContext) {
        this.facotry = facotry;
        this.serviceRequestContext = serviceRequestContext;
    }

    public boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
        if (connection != null) {
            connection.setAutoCommit(autoCommit);
        }
    }

    protected void openConnection() throws SQLException {
        connection = facotry.createConnection();
        originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(autoCommit);
    }

    protected void closeConnection() throws SQLException {
        connection.setAutoCommit(originalAutoCommit);
        connection.close();
        connection = null;
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
        if (connection != null) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }
}
