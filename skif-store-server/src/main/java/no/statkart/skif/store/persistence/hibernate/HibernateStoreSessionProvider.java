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
public class HibernateStoreSessionProvider implements Provider<HibernateStoreSession> {
    private final HibernateStoreSessionManager storeSessionManager;
    private final ReplicaVersion replicaVersion;

    @Inject
    public HibernateStoreSessionProvider(HibernateStoreSessionManager storeSessionManager, ReplicaVersion replicaVersion) {
        this.storeSessionManager = storeSessionManager;
        this.replicaVersion = replicaVersion;
    }

    public HibernateStoreSession get() {
        return storeSessionManager.getSession(replicaVersion);
    }
}
