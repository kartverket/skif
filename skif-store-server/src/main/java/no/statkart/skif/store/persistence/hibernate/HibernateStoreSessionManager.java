package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.StoreSessionManager;

/**
 * @author Henrik Fredholm
 */
public interface  HibernateStoreSessionManager extends StoreSessionManager, HibernateSessionManager {
    @Override
    HibernateStoreSession getStoreSession(SnapshotVersion snapshotVersion);
    @Override
    void beginSnapshotScope(SnapshotVersion snapshotVersion);
    @Override
    void endSnapshotScope();
    @Override
    HibernateStoreSession acquireSnapshotStoreSessionUsingSnapshotScope();
    @Override
    HibernateStoreSession acquireSnapshotStoreSession(SnapshotVersion snapshotVersion);
    @Override
    void releaseSnapshotStoreSession(StoreSession storeSession);
}
