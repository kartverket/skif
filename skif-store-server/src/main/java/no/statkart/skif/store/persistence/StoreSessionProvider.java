package no.statkart.skif.store.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionManager;
import org.hibernate.Session;

import java.sql.SQLException;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreSessionProvider implements Provider<StoreSession> {
    private final ReplicaVersion key;
    private StoreSessionManager storeSessionManager;

    @Inject
    public StoreSessionProvider(ReplicaVersion key) {
        this.key = key;
    }

    public StoreSessionProvider(StoreSessionManager storeSessionManager, ReplicaVersion key) {
        this.key = key;
        this.storeSessionManager = storeSessionManager;
    }

    @Inject
    public void setStoreSessionManager(StoreSessionManager hibernateSessionManager) {
        this.storeSessionManager = storeSessionManager;
    }

    public StoreSession get() {
        try {
            return storeSessionManager.getStoreSession(key);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
