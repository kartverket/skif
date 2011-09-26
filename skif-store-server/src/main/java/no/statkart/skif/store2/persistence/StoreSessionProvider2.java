package no.statkart.skif.store2.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.ReplicaVersion2;

import java.sql.SQLException;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreSessionProvider2 implements Provider<StoreSession2> {
    private final ReplicaVersion2 key;
    private StoreSessionManager2 storeSessionManager;

    @Inject
    public StoreSessionProvider2(ReplicaVersion2 key) {
        this.key = key;
    }

    public StoreSessionProvider2(StoreSessionManager2 storeSessionManager, ReplicaVersion2 key) {
        this.key = key;
        this.storeSessionManager = storeSessionManager;
    }

    @Inject
    public void setStoreSessionManager(StoreSessionManager2 hibernateSessionManager) {
        this.storeSessionManager = storeSessionManager;
    }

    public StoreSession2 get() {
        try {
            return storeSessionManager.getStoreSession(key);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
