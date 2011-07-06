package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.ReplicaVersion;

/**
 * Guice Provider implementasjon som gir ut et Hibernate WrapperSession objekt som hentes fra
 * HibernateSessionWrapperManager som provideren initialiseres med. Provideren initialiseres også med hvilken
 * ReplicaVersion som skal hentes ut.
 *
 * @author Henrik Fredholm
 * @since 0.2
 */
public class StoreHibernateSessionProvider implements Provider<StoreHibernateSession> {
    private final StoreHibernateSessionManager sessionManager ;
    private final ReplicaVersion replicaVersion;

    @Inject
    public StoreHibernateSessionProvider(StoreHibernateSessionManager sessionManager, ReplicaVersion replicaVersion) {
        this.sessionManager = sessionManager;
        this.replicaVersion = replicaVersion;
    }

    public StoreHibernateSession get() {
        return sessionManager.getSession(replicaVersion);
    }
}
