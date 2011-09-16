package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;

import java.sql.SQLException;

/**
 * Guice Provider som gir ut et  HibernateStoreSession objekt som hentes fra en HibernateStoreSessionManager.
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
        try {
            return storeSessionManager.getStoreSession(replicaVersion);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
