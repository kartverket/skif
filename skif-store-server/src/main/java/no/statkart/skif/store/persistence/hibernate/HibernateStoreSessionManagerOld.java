package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.StoreSessionManager;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Holder et array av HibernateStoreSession objekter. Det er et session for hver ReplicaVersion verdi. Har også
 * en Manageren kan opprette en HibernateStoreSession (med tilhørende underliggende Hibernate Session og SessionFactory) hvis
 * for en gitt ReplicaVersion verdi. Det er mulig å spørre manageren om det har blit opprettet et HibernateStoreSession
 * objekt for en gitt ReplicaVersion verdi.
 * <p/>
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Deprecated
public class HibernateStoreSessionManagerOld implements StoreSessionManager {
    protected boolean isTransactional;
    protected Transaction localTranaction;
    protected boolean allowSharing = true;
    protected final HibernateStoreSession[] storeSessions = new HibernateStoreSession[ReplicaVersion.values().length];
    protected final HibernateStoreSessionFactoryManager factoryManagerStore;

    @Inject
    public HibernateStoreSessionManagerOld(HibernateStoreSessionFactoryManager factoryManagerStore) {
        this.factoryManagerStore = factoryManagerStore;
    }

    public boolean hasActiveSession(ReplicaVersion replicaVersion) {
        return storeSessions[replicaVersion.ordinal()] != null;
    }


    protected HibernateStoreSession openSession(ReplicaVersion replicaVersion) {
        SessionFactory sessionFactory = factoryManagerStore.getFactory(replicaVersion);
        return storeSessions[replicaVersion.ordinal()] = new HibernateStoreSession(sessionFactory.openSession(), replicaVersion);
    }

    public void closeSession(ReplicaVersion replicaVersion) {
        if (hasActiveSession(replicaVersion)) {
            HibernateStoreSession storeSessionWrapper = storeSessions[replicaVersion.ordinal()];
            storeSessionWrapper.getWrappedSession().flush();
            storeSessionWrapper.getWrappedSession().close();
            storeSessions[replicaVersion.ordinal()] = null;
        }
    }

    public void closeSession() {
        for (ReplicaVersion replicaVersion : ReplicaVersion.values()) {
            closeSession(replicaVersion);
        }
    }

    public void markTransactional() {
        isTransactional = true;
    }

    public HibernateStoreSession getSession(ReplicaVersion replicaVersion) {
        HibernateStoreSession storeSession = storeSessions[replicaVersion.ordinal()];
        if (storeSession == null) {
            storeSessions[replicaVersion.ordinal()] = storeSession = openSession(replicaVersion);
        } else {
            if (!allowSharing) {
                throw new ImplementationException("Cannot create Hibernate Session. Connection already in use and sharing is disallowed");
            }
        }
        return storeSession;
    }

    public void flushSession(ReplicaVersion replicaVersion) {
        if (hasActiveSession(replicaVersion)) {
            HibernateStoreSession storeSession = storeSessions[replicaVersion.ordinal()];
            storeSession.getWrappedSession().flush();
        }
    }

    public void flushAllSessions() {
        for (ReplicaVersion replicaVersion : ReplicaVersion.values()) {
            flushSession(replicaVersion);
        }
    }

    @Override
    public Connection getConnection(Object key) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isActive(Object key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close(Object key) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void beginTransaction() {
        markTransactional();
        HibernateStoreSession storeSession = getSession(ReplicaVersion.CURRENT);
        localTranaction = storeSession.getWrappedSession().beginTransaction();
    }

    public void commit() {
        localTranaction.commit();
    }

    @Override
    public void rollback() {
        localTranaction.rollback();
    }

    public Connection getConnection(ReplicaVersion replicaVersion, boolean allowSharing) {
        Connection connection = getSession(replicaVersion).getWrappedSession().connection();
        this.allowSharing &= allowSharing;
        return connection;
    }

    @Override
    public StoreSession getStoreSession(Object key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
