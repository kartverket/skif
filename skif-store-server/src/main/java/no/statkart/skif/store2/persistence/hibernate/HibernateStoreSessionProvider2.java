package no.statkart.skif.store2.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.ReplicaVersion2;

import java.sql.SQLException;

/**
 * Guice Provider som gir ut et  HibernateStoreSession2 objekt som hentes fra en HibernateStoreSessionManager2.
 *
 * @author Henrik Fredholm
 * @since 0.2
 */
public class HibernateStoreSessionProvider2 implements Provider<HibernateStoreSession2> {
    private final HibernateStoreSessionManager2 storeSessionManager;
    private final ReplicaVersion2 replicaVersion;


    @Inject
    public HibernateStoreSessionProvider2(HibernateStoreSessionManager2 storeSessionManager, ReplicaVersion2 replicaVersion) {
        this.storeSessionManager = storeSessionManager;
        this.replicaVersion = replicaVersion;
    }

    public HibernateStoreSession2 get() {
        try {
            return storeSessionManager.getStoreSession(replicaVersion);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
