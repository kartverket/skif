package no.statkart.skif.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
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
    private boolean originalAutoCommit;


    @Inject
    public ConnectionManagerSingleVm(ConnectionFactory facotry, ServiceRequestContext serviceRequestContext) {
        this.facotry = facotry;
        this.serviceRequestContext = serviceRequestContext;
    }

    protected void openConnection() {
        try {
            connection = facotry.createConnection();
            originalAutoCommit = connection.getAutoCommit();
            if (originalAutoCommit) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    protected void closeConnection() {
        try {
            connection.setAutoCommit(originalAutoCommit);
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public Connection getConnection(Object key) {
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
    public void close(Object key) {
        if (connection != null) {
            closeConnection();
        }
    }

    @Override
    public void close() {
        close(null);
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void commit() {
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }
    }

    @Override
    public void rollback() {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
