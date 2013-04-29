package no.statkart.skif.service;

import no.statkart.skif.service.scope.ServiceRequestScoped;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.Serializable;
import java.security.Principal;

/**
 * Denne klasseninneholder infomasjon om inneværende kall som vedlikeholdes av servicerammeverket.
 * @author Henrik Fredholm
 * @since 2.0
 */
@ServiceRequestScoped
public class ServiceRequestContext implements Serializable {
    private java.security.Principal callerPrincipal;
    private String servicename;
    private long parentCallId;
    private long callId;
    private int nestedLevel;
    private TxMode txMode;
    private final boolean beanManagedTransaction;
    private final TransactionAttributeType transactionAttributeType;
    private boolean rollbackOnly;

    public ServiceRequestContext() {
        this(TxMode.NOT_IN_EJB, false, null);
    }

    public ServiceRequestContext(TxMode txMode, boolean beanManagedTransaction, TransactionAttributeType transactionAttributeType) {
        this.txMode = txMode;
        this.beanManagedTransaction = beanManagedTransaction;
        this.transactionAttributeType = transactionAttributeType;
    }

    public ServiceRequestContext(ServiceRequestContext context, TxMode txMode, boolean beanManagedTransaction, TransactionAttributeType transactionAttributeType) {
        this.callerPrincipal = context.callerPrincipal;
        this.servicename = context.servicename;
        this.parentCallId = context.parentCallId;
        this.callId = context.callId;
        this.nestedLevel = context.nestedLevel;
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
        return txMode== TxMode.TX;
    }

    public boolean isContinuedTx() {
        return txMode== TxMode.TX_CONTINUATION;
    }

    public boolean inTx() {
        return txMode != TxMode.NO_TX;
    }

    public Principal getCallerPrincipal() {
        return callerPrincipal;
    }

    public void setCallerPrincipal(Principal callerPrincipal) {
        this.callerPrincipal = callerPrincipal;
    }

    public int getNestedLevel() {
        return nestedLevel;
    }

    public void setNestedLevel(int nestedLevel) {
        this.nestedLevel = nestedLevel;
    }

    public String getUserName() {
        return callerPrincipal.getName();
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public long getCallId() {
        return callId;
    }

    public long getParentCallId() {
        return parentCallId;
    }

    public void setParentCallId(long parentCallId) {
        this.parentCallId = parentCallId;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getServicename() {
        return servicename;
    }

    public void incNestedLevel() {
        nestedLevel++;
    }

    public void setFrom(ServiceRequestContext context) {
        this.callerPrincipal = context.callerPrincipal;
        this.callId = context.callId;
        this.nestedLevel= context.nestedLevel;
        this.parentCallId = context.parentCallId;
        this.servicename = context.servicename;
        this.txMode = context.txMode;
    }

    public boolean isContinuation() {
        return txMode==TxMode.NO_TX_CONTINUATION || txMode == TxMode.TX_CONTINUATION;
    }

    public boolean isTransactional() {
        return txMode==TxMode.TX|| txMode == TxMode.TX_CONTINUATION;
    }

    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }
}
