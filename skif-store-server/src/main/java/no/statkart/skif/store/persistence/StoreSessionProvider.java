package no.statkart.skif.store.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreSessionProvider implements Provider<StoreSession> {
    private final SnapshotVersion key;
    private StoreSessionManager storeSessionManager;

    @Inject
    public StoreSessionProvider(SnapshotVersion key) {
        this.key = key;
    }

    public StoreSessionProvider(StoreSessionManager storeSessionManager, SnapshotVersion key) {
        this.key = key;
        this.storeSessionManager = storeSessionManager;
    }

    @Inject
    public void setStoreSessionManager(StoreSessionManager hibernateSessionManager) {
        this.storeSessionManager = storeSessionManager;
    }

    public StoreSession get() {
        return storeSessionManager.getStoreSession(key);
    }
}
