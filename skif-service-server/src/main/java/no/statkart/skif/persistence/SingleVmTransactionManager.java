package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.persistence.jdbc.NonTransactional;

import javax.sql.DataSource;
import javax.transaction.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Enkel implementasjon av JTA TransactionManager for single-vm.
 */
@Singleton
public class SingleVmTransactionManager implements TransactionManager {
    private final ThreadLocal<SingleVmTransaction> transactions = new ThreadLocal<>();

    private final DataSource dataSource;

    @Inject
    public SingleVmTransactionManager(@NonTransactional DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    void unset(SingleVmTransaction transaction) throws SystemException {
        SingleVmTransaction active = transactions.get();
        if (active != transaction) {
            throw new SystemException("Invalid transaction");
        }
        transactions.remove();
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        SingleVmTransaction transaction = transactions.get();
        if (transaction != null && transaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
            throw new NotSupportedException("Transaction already active");
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            transaction = new SingleVmTransaction(this, connection);
            transactions.set(transaction);
        } catch (SQLException e) {
            SystemException systemException = new SystemException("Could not get JDBC connection for transaction");
            systemException.initCause(e);

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                    systemException.addSuppressed(e1);
                }
            }

            throw systemException;
        }
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        SingleVmTransaction transaction = transactions.get();
        if (transaction == null) {
            throw new IllegalStateException("No active transaction");
        }
        transaction.commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        SingleVmTransaction transaction = transactions.get();
        if (transaction == null) {
            throw new IllegalStateException("No active transaction");
        }
        transaction.rollback();
    }

    @Override
    public int getStatus() throws SystemException {
        SingleVmTransaction transaction = transactions.get();
        if (transaction != null) {
            return transaction.getStatus();
        } else {
            return Status.STATUS_NO_TRANSACTION;
        }
    }

    @Override
    public SingleVmTransaction getTransaction() throws SystemException {
        return transactions.get();
    }

    @Override
    public void resume(Transaction tobj) throws InvalidTransactionException, IllegalStateException, SystemException {
        SingleVmTransaction transaction = transactions.get();
        if (transaction != null && transaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("Transaction already active");
        }
        try {
            SingleVmTransaction singleVmTransaction = (SingleVmTransaction) tobj;
            if (singleVmTransaction != null && singleVmTransaction.getTransactionManager() != this) {
                throw new InvalidTransactionException("Transaction belongs to another transaction manager");
            }
            transactions.set(singleVmTransaction);
        } catch (ClassCastException e) {
            throw new InvalidTransactionException(e.getMessage());
        }
    }

    @Override
    public Transaction suspend() throws SystemException {
        SingleVmTransaction transaction = transactions.get();
        transactions.remove();
        return transaction;
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        Transaction transaction = getTransaction();
        if (transaction == null) {
            throw new IllegalStateException("No active transaction");
        }
        transaction.setRollbackOnly();
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        // Ikke implementert
    }
}
