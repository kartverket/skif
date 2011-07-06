package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.ReplicaVersion;
import org.hibernate.Session;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateSessionProvider implements Provider<Session> {
    private final StoreHibernateSessionManager sessionManager ;
    private final ReplicaVersion replicaVersion;

    @Inject
    public HibernateSessionProvider(StoreHibernateSessionManager sessionManager, ReplicaVersion replicaVersion) {
        this.sessionManager = sessionManager;
        this.replicaVersion = replicaVersion;
    }

    public Session get() {
        return sessionManager.getSession(replicaVersion).getHibernateSession();
    }
}
