package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.store3.persistence.PersistenceDescriptorWithStack;
import no.statkart.skif.store3.persistence.SnapshotSessionEventSource;
import org.hibernate.Session;
import org.hibernate.Transaction;


/**
 * @author Henrik Fredholm
 */
public class HibernateSessionDescriptor extends PersistenceDescriptorWithStack<Session, HibernateSessionFactoryDescriptor> {
    private final SnapshotSessionEventSource eventSource;
    private boolean autoCloseSession = false;
    private boolean inTransactionalContext;
    private boolean originalAutoCommit;
    private Transaction hibernateTransaction;
    private boolean useLocalTransaction = true; // Hardkodet midlertidig

    public HibernateSessionDescriptor(HibernateSessionFactoryDescriptor wrapped, SnapshotSessionEventSource eventSource) {
        super(wrapped);
        this.eventSource = eventSource;
    }

    public SnapshotSessionEventSource getEventSource() {
        return eventSource;
    }

    public boolean isAutoCloseSession() {
        return autoCloseSession;
    }

    public void setAutoCloseSession(boolean autoCloseSession) {
        this.autoCloseSession = autoCloseSession;
    }

    public boolean isInTransactionalContext() {
        return inTransactionalContext;
    }

    public void setInTransactionalContext(boolean inTransactionalContext) {
        this.inTransactionalContext = inTransactionalContext;
    }

    public boolean getOriginalAutoCommit() {
        return originalAutoCommit;
    }

    public void setOriginalAutoCommit(boolean originalAutoCommit) {
        this.originalAutoCommit = originalAutoCommit;
    }

    public Transaction getHibernateTransaction() {
        return hibernateTransaction;
    }

    public void setHibernateTransaction(Transaction hibernateTransaction) {
        this.hibernateTransaction = hibernateTransaction;
    }

    public boolean isUseLocalTransaction() {
        return useLocalTransaction;
    }

    public void setUseLocalTransaction(boolean useLocalTransaction) {
        this.useLocalTransaction = useLocalTransaction;
    }
}
