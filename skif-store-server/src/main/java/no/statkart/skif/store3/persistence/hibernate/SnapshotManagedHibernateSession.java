package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store3.persistence.*;
import org.hibernate.Interceptor;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Forslag til nytt pattern:
 * try {
 * sessionManager.enterScope();
 * Session session1 = sessionManager.acquireLockedForSnapshot(SnapshotVersion.CURRENT);
 * Session sesiosn2 = sessionManager.acquireLockedForSnapshot(SnapshotVersion.OLD);
 * assertNotSame(session1,session2);
 * <p/>
 * Session session3 = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
 * assertSame(session1, session3);
 * Session session4 = sessionManager.acquireForSnapshot(SnapshotVersion.OLD);
 * assertSame(session2, session4);
 * } finally {
 * sessionManager.exitScope();
 * }
 *
 * @author Henrik Fredholm
 */
public class SnapshotManagedHibernateSession implements SnapshotManaged<Session>, TransactionalResource {
    private final HibernateSessionFactoryManager sessionFactoryManager;
    private final HibernateSessionFactoryDescriptor[] sessionFactoryDescriptors;
    private final HibernateSessionDescriptor[] sessionDescriptors;
    private final boolean supportsUpdate;

    public SnapshotManagedHibernateSession(HibernateSessionFactoryManager sessionFactoryManager) {
        this.sessionFactoryManager = sessionFactoryManager;
        this.sessionFactoryDescriptors = sessionFactoryManager.getPersistenceDescriptors();
        this.sessionDescriptors = new HibernateSessionDescriptor[sessionFactoryDescriptors.length];
        this.supportsUpdate = sessionFactoryDescriptors.length > 0 && sessionFactoryDescriptors[0].getSeed().get() == SnapshotVersion.CURRENT;

        for (int i = 0; i < sessionFactoryDescriptors.length; i++) {
            HibernateSessionFactoryDescriptor hibernateSessionFactoryDescriptor = sessionFactoryDescriptors[i];
            this.sessionDescriptors[i] = new HibernateSessionDescriptor(hibernateSessionFactoryDescriptor, new SnapshotSessionEventSourceImpl());
        }
    }

    /**
     * Pusher nåværende snapshotVersion på stacken for valgt session. Dersom den nye snapshotVersionen er anderledes
     * enn nåværende for sessionen sendes et snapshot event til alle listeners før det nye snapshot settes på sessionen.
     *
     * @param snapshotVersion
     * @return session med ønsket snapshotVersion satt
     */
    @Override
    public Session acquireForSnapshot(SnapshotVersion snapshotVersion) {
        Session session = findExistingSnapshotVersionSession(snapshotVersion);
        if (session == null) {
            session = allocateSession(snapshotVersion);
        }
        if (session == null) {
            session = reuseSession(snapshotVersion);
        }
        return session;
    }

    private Session findExistingSnapshotVersionSession(SnapshotVersion snapshotVersion) {
        Session session = null;
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getSeed().get().equals(snapshotVersion)) {
                if (sessionDescriptor.getObject() == null) {
                    session = createSession(sessionDescriptor);
                    sessionDescriptor.getWrapped().setSnapshotVersion(session, snapshotVersion);
                    sessionDescriptor.pushSnapshotVersion(snapshotVersion);
                }
            }
        }
        return session;
    }

    private Session createSession(HibernateSessionDescriptor sessionDescriptor) {
        Session session;
        HibernateSessionFactoryDescriptor sessionFactoryDescriptor = sessionDescriptor.getWrapped();
        Interceptor hibernateInterceptor = sessionFactoryDescriptor.getHibernateInterceptor();
        if (hibernateInterceptor == null) {
            session = sessionFactoryManager.getFactory(sessionFactoryDescriptor.getIndex()).openSession();
        } else {
            session = sessionFactoryManager.getFactory(sessionFactoryDescriptor.getIndex()).openSession(hibernateInterceptor);
        }
        sessionDescriptor.setObject(session);

        if (sessionDescriptor.isInTransactionalContext()) {
            beginTransaction(sessionDescriptor);
        }
        return session;
    }

    private void closeSession(HibernateSessionDescriptor sessionDescriptor) {
        sessionDescriptor.getEventSource().fireOnClose();
        sessionDescriptor.getObject().close();
        sessionDescriptor.setObject(null);
    }

    private Session allocateSession(SnapshotVersion snapshotVersion) {
        Session session = null;
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getObject() == null && sessionDescriptor.getWrapped().accepts(snapshotVersion)) {
                session = createSession(sessionDescriptor);
                sessionDescriptor.getWrapped().setSnapshotVersion(session, snapshotVersion);
                SnapshotVersion oldValue = sessionDescriptor.getSeed().set(snapshotVersion);
                sessionDescriptor.pushSnapshotVersion(oldValue);
            }
        }
        return session;
    }

    private Session reuseSession(SnapshotVersion snapshotVersion) {
        Session session = null;
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getWrapped().accepts(snapshotVersion)) {
                session = sessionDescriptor.getObject();
                sessionDescriptor.getEventSource().fireOnChangeSnapshot();
                sessionDescriptor.getWrapped().setSnapshotVersion(session, snapshotVersion);
                SnapshotVersion oldValue = sessionDescriptor.getSeed().set(snapshotVersion);
                sessionDescriptor.pushSnapshotVersion(oldValue);
            }
        }
        return session;
    }

    /**
     * Popper forrige snapshot fra stakken. Dersom den poppede verdien er anderledes
     * enn den nåværende settes denne på sessionen. Ved endring av Snapshot for sessionen sendes en event ut
     * i forkant til alle listenere om at snapshot version kommer til å bli endret.
     *
     * @param session
     */
    @Override
    public Session releaseForSnapshot(Session session) {
        if (session == null) return null;

        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getObject() == session) {
                boolean isChanging = false;
                try {
                    isChanging = sessionDescriptor.peekSnapshotVersion().equals(sessionDescriptor.getSeed().get());
                    if (isChanging) {
                        sessionDescriptor.getEventSource().fireOnChangeSnapshot();
                    }
                } finally {
                    SnapshotVersion snapshotVersion = sessionDescriptor.popSnapshotVersion();
                    if (isChanging) {
                        sessionDescriptor.getSeed().set(snapshotVersion);
                    }
                }
                break;
            }
        }
        return null;
    }

    @Override
    public HibernateSessionDescriptor[] getPersistenceDescriptors() {
        return sessionDescriptors;

    }

    public int getPersistenceDescriptorIndex(Session session) {
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getObject() == session) {
                return i;
            }
        }
        return -1; // Not found
    }

    public void close() {
        // TODO: mer robust exception håndtering. Må sikre at alle session alltid blir lukket ved exception
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.isInTransactionalContext()) {
                throw new ImplementationException("TransactionContext er ikke avsluttet : " + sessionDescriptor);
            }
            if (sessionDescriptor.peekSnapshotVersion() != null) {
                throw new ImplementationException("SnapshotVersion stakk er ikke tom: " + sessionDescriptor);
            }

            if (sessionDescriptor.getObject() != null) {
                closeSession(sessionDescriptor);
            }
        }
    }

    public void close(Session session) {
        HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[getPersistenceDescriptorIndex(session)];
        if (sessionDescriptor.peekSnapshotVersion() == null) {
            closeSession(sessionDescriptor);
        } else {
            sessionDescriptor.setAutoCloseSession(true);
        }
    }

    public void flush() {
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getObject() != null) {
                sessionDescriptor.getObject().flush();
            }
        }
    }

    public void flush(Session session) {
        HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[getPersistenceDescriptorIndex(session)];
        sessionDescriptor.getObject().flush();
    }

    public void clear() {
        for (int i = 0; i < sessionDescriptors.length; i++) {
            HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[i];
            if (sessionDescriptor.getObject() != null) {
                sessionDescriptor.getEventSource().fireOnClear();
                sessionDescriptor.getObject().clear();
            }
        }
    }

    public void clear(Session session) {
        HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[getPersistenceDescriptorIndex(session)];
        sessionDescriptor.getEventSource().fireOnClear();
        sessionDescriptor.getObject().clear();
    }

    public void beginTransaction() {
        Preconditions.checkState(supportsUpdate);
        HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[0];
        Preconditions.checkState(!sessionDescriptor.isInTransactionalContext());

        sessionDescriptor.setInTransactionalContext(true);
        if (sessionDescriptor.getObject() != null) {
            beginTransaction(sessionDescriptor);
        }
    }

    public void commit() {
        Preconditions.checkState(supportsUpdate);
        HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[0];
        Preconditions.checkState(sessionDescriptor.isInTransactionalContext());
        Session session = sessionDescriptor.getObject();
        if (sessionDescriptor.getObject() != null) {
            commit(sessionDescriptor);
        }
        sessionDescriptor.setInTransactionalContext(false);
    }

    public void rollback() {
        Preconditions.checkState(supportsUpdate);
        HibernateSessionDescriptor sessionDescriptor = sessionDescriptors[0];
        Preconditions.checkState(sessionDescriptor.isInTransactionalContext());
        if (sessionDescriptor.getObject() != null) {
            rollback(sessionDescriptor);
        }
        sessionDescriptor.setInTransactionalContext(false);
    }

    private void beginTransaction(HibernateSessionDescriptor sessionDescriptor) {
        try {
            setAutoCommitMode(sessionDescriptor);
            if (sessionDescriptor.isUseLocalTransaction()) {
                sessionDescriptor.setHibernateTransaction(sessionDescriptor.getObject().beginTransaction());
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    private void commit(HibernateSessionDescriptor sessionDescriptor) {
        try {
            if (sessionDescriptor.isUseLocalTransaction()) {
                sessionDescriptor.getHibernateTransaction().commit();
            }
            resetAutoCommitMode(sessionDescriptor);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    private void rollback(HibernateSessionDescriptor sessionDescriptor) {
        try {
            if (sessionDescriptor.isUseLocalTransaction()) {
                sessionDescriptor.getHibernateTransaction().rollback();
            }
            resetAutoCommitMode(sessionDescriptor);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }


    private void setAutoCommitMode(HibernateSessionDescriptor sessionDescriptor) throws SQLException {
        Connection connection = sessionDescriptor.getObject().connection();
        sessionDescriptor.setOriginalAutoCommit(connection.getAutoCommit());
        if (sessionDescriptor.getOriginalAutoCommit()) {
            connection.setAutoCommit(false);
        }
    }

    private void resetAutoCommitMode(HibernateSessionDescriptor sessionDescriptor) throws SQLException {
        sessionDescriptor.getObject().connection().setAutoCommit(sessionDescriptor.getOriginalAutoCommit());
    }
}
