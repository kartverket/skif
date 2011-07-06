package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.StoreSessionManager;
import org.hibernate.SessionFactory;

/**
 * Holder et array av StoreHibernateSession objekter. Det er et session for hver ReplicaVersion verdi. Har også
 * en Manageren kan opprette en StoreHibernateSession (med tilhørende underliggende Hibernate Session og SessionFactory) hvis
 * for en gitt ReplicaVersion verdi. Det er mulig å spørre manageren om det har blit opprettet et StoreHibernateSession
 * objekt for en gitt ReplicaVersion verdi.
 * <p/>
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreHibernateSessionManager implements StoreSessionManager {
    protected final StoreHibernateSession[] sessions = new StoreHibernateSession[ReplicaVersion.values().length];
    protected final StoreHibernateSessionFactoryManager factoryManager;

    @Inject
    public StoreHibernateSessionManager(StoreHibernateSessionFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public boolean hasActiveSession(ReplicaVersion replicaVersion) {
        return sessions[replicaVersion.ordinal()]!=null;
    }


    protected StoreHibernateSession openSession(ReplicaVersion replicaVersion) {
        SessionFactory sessionFactory = factoryManager.getFactory(replicaVersion);
        return sessions[replicaVersion.ordinal()] = new StoreHibernateSession(sessionFactory.openSession(), replicaVersion);
    }

    public void closeSession(ReplicaVersion replicaVersion) {
        if (hasActiveSession(replicaVersion)) {
            StoreHibernateSession sessionWrapper = sessions[replicaVersion.ordinal()];
            sessionWrapper.getHibernateSession().flush();
            sessionWrapper.getHibernateSession().close();
            sessions[replicaVersion.ordinal()] = null;
        }
    }

    @Override
    public void closeAllSessions() {
        for (ReplicaVersion replicaVersion : ReplicaVersion.values()) {
            closeSession(replicaVersion);
        }
    }

    public StoreHibernateSession getSession(ReplicaVersion replicaVersion) {
        StoreHibernateSession session = sessions[replicaVersion.ordinal()];
        if (session==null) {
           sessions[replicaVersion.ordinal()] = session = openSession(replicaVersion);
        }
        return session;
    }

    @Override
    public void flushSession(ReplicaVersion replicaVersion) {
        if (hasActiveSession(replicaVersion)) {
            StoreHibernateSession storeSession = sessions[replicaVersion.ordinal()];
            storeSession.getHibernateSession().flush();
        }
    }

    @Override
    public void flushAllSessions() {
        for (ReplicaVersion replicaVersion : ReplicaVersion.values()) {
            flushSession(replicaVersion);
        }
    }
}
