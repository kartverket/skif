package no.statkart.skif.persistence;

import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Enkel implementasjon av JTA-transaksjon for single-vm som kun støtter én enkelt ressurs, og det er JDBC.
 */
public class SingleVmTransaction implements Transaction {
    private final SingleVmTransactionManager transactionManager;
    private Connection connection;
    private boolean rollbackOnly = false;

    private List<Synchronization> synchronizations = new ArrayList<>();

    SingleVmTransaction(SingleVmTransactionManager transactionManager, Connection connection) {
        this.transactionManager = transactionManager;
        this.connection = connection;
    }

    SingleVmTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        throw new SystemException("Not supported");
    }

    @Override
    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        throw new SystemException("Not supported");
    }

    @Override
    public int getStatus() throws SystemException {
        return connection != null ? (rollbackOnly ? Status.STATUS_MARKED_ROLLBACK : Status.STATUS_ACTIVE) : Status.STATUS_NO_TRANSACTION;
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        if (rollbackOnly) {
            rollback();
            throw new RollbackException("Transaction marked rollback-only");
        }

        if (connection == null) {
            throw new IllegalStateException("No active transaction");
        }

        for (Synchronization synchronization : synchronizations) {
            synchronization.beforeCompletion();
        }

        int status = Status.STATUS_ROLLEDBACK;

        try {
            connection.commit();
            status = Status.STATUS_COMMITTED;
        } catch (SQLException e) {
            HeuristicMixedException exception = new HeuristicMixedException("Error during commit");
            exception.initCause(e);

            try {
                connection.rollback();
            } catch (SQLException e1) {
                exception.addSuppressed(e1);
            }

            throw exception;
        } finally {
            for (Synchronization synchronization : synchronizations) {
                synchronization.afterCompletion(status);
            }

            try {
                connection.close();
            } catch (SQLException e) {
                SystemException systemException = new SystemException("Could not close connection");
                systemException.initCause(e);
                throw systemException;
            }
            connection = null;
            transactionManager.unset(this);
        }

    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {
        if (connection == null) {
            throw new IllegalStateException("No active transaction");
        }

        try {
            connection.rollback();

            for (Synchronization synchronization : synchronizations) {
                synchronization.afterCompletion(Status.STATUS_ROLLEDBACK);
            }
        } catch (SQLException e) {
            SystemException exception = new SystemException("Error during rollback");
            exception.initCause(e);
            throw exception;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                SystemException systemException = new SystemException("Could not close connection");
                systemException.initCause(e);
                throw systemException;
            }
            connection = null;
            transactionManager.unset(this);
        }
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        if (connection == null) {
            throw new IllegalStateException("No active transaction");
        }
        rollbackOnly = true;
    }

    @Override
    public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        synchronizations.add(sync);
    }
}
