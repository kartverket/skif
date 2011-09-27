package no.statkart.skif.store2.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.persistence.StoreSession2;
import no.statkart.skif.store2.persistence.StoreSessionManager2;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public interface HibernateStoreSessionManager2 extends StoreSessionManager2, HibernateSessionManager2 {
    @Override
    HibernateStoreSession2 getStoreSession(SnapshotVersion snapshotVersion);
    @Override
    void beginSnapshotScope(SnapshotVersion snapshotVersion);
    @Override
    void endSnapshotScope();
    @Override
    HibernateStoreSession2 acquireSnapshotStoreSessionUsingSnapshotScope();
    @Override
    HibernateStoreSession2 acquireSnapshotStoreSession(SnapshotVersion snapshotVersion);
    @Override
    void releaseSnapshotStoreSession(StoreSession2 storeSession);}
