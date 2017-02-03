package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.transaction.*;

/**
 * Enkel implementasjon av JTA UserTransaction for single-vm.
 */
@Singleton
public class SingleVmUserTransaction implements UserTransaction {
    private final SingleVmTransactionManager transactionManager;

    @Inject
    public SingleVmUserTransaction(SingleVmTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        transactionManager.begin();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        transactionManager.commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        transactionManager.rollback();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        transactionManager.setRollbackOnly();
    }

    @Override
    public int getStatus() throws SystemException {
        return transactionManager.getStatus();
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        transactionManager.setTransactionTimeout(seconds);
    }
}
