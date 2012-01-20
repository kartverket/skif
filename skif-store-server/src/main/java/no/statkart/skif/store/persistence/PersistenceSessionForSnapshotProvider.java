package no.statkart.skif.store.persistence;

import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class PersistenceSessionForSnapshotProvider implements Provider<PersistenceSessionForSnapshot> {
    private final PersistenceSessionManager persistenceSessionManager;

    public PersistenceSessionForSnapshotProvider(PersistenceSessionManager persistenceSessionManager) {
        this.persistenceSessionManager = persistenceSessionManager;
    }

    @Override
    public PersistenceSessionForSnapshot get() {
        return persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
    }
}
