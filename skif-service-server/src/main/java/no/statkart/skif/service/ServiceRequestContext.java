package no.statkart.skif.service;

import com.google.inject.Provider;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.scope.ServiceRequestScoped;

import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.security.Principal;

/**
 * Denne klasseninneholder infomasjon om inneværende kall som vedlikeholdes av servicerammeverket.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@ServiceRequestScoped
public class ServiceRequestContext implements Serializable {
    private final java.security.Principal callerPrincipal;
    private final long callId;
    @Nullable
    private final ServiceRequestContext parent;
    private TxMode txMode;
    private final boolean beanManagedTransaction;
    private final TransactionAttributeType transactionAttributeType;
    private boolean rollbackOnly;

    @Inject
    public ServiceRequestContext(@CallId Provider<Long> callIdProvider) {
        this(null, callIdProvider.get());
    }

    public ServiceRequestContext(java.security.Principal principal, long callId) {
        this(principal, callId, TxMode.NOT_IN_EJB, false, null);
    }

    public ServiceRequestContext(java.security.Principal callerPrincipal, long callId, TxMode txMode, boolean beanManagedTransaction, TransactionAttributeType transactionAttributeType) {
        this.callerPrincipal = callerPrincipal;
        this.callId = callId;
        this.parent = null;
        this.txMode = txMode;
        this.beanManagedTransaction = beanManagedTransaction;
        this.transactionAttributeType = transactionAttributeType;
    }

    public ServiceRequestContext(ServiceRequestContext parent, long callId, TxMode txMode, boolean beanManagedTransaction, TransactionAttributeType transactionAttributeType) {
        this.callerPrincipal = parent.callerPrincipal;
        this.callId = callId;
        this.parent = parent;
        this.txMode = txMode;
        this.beanManagedTransaction = beanManagedTransaction;
        this.transactionAttributeType = transactionAttributeType;
    }

    public TxMode getTxMode() {
        return txMode;
    }

    public void setTxMode(TxMode txMode) {
        this.txMode = txMode;
    }

    public TransactionAttributeType getTransactionAttributeType() {
        return transactionAttributeType;
    }

    public boolean isBeanManagedTransaction() {
        return beanManagedTransaction;
    }

    public boolean isContainerManagedTransaction() {
        return !beanManagedTransaction;
    }

    public boolean isNewTx() {
        return txMode == TxMode.TX;
    }

    public boolean isContinuedTx() {
        return txMode == TxMode.TX_CONTINUATION;
    }

    public boolean inTx() {
        return txMode == TxMode.TX || txMode == TxMode.TX_CONTINUATION;
    }

    public Principal getCallerPrincipal() {
        return callerPrincipal;
    }

    public String getUserName() {
        return callerPrincipal == null ? null : callerPrincipal.getName();
    }

    public long getCallId() {
        return callId;
    }

    @Nullable
    public ServiceRequestContext getParent() {
        return parent;
    }

    public long getParentCallId() {
        return parent != null ? parent.getCallId() : 0;
    }

    public boolean isContinuation() {
        return txMode == TxMode.NO_TX_CONTINUATION || txMode == TxMode.TX_CONTINUATION;
    }

    public boolean isTransactional() {
        return txMode == TxMode.TX || txMode == TxMode.TX_CONTINUATION;
    }

    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }
}
