package no.statkart.skif.store5.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionManager extends PersistenceSession {
    PersistenceSessionForSnapshot getForSnapshot(SnapshotVersion snapshotVersion);
    PersistenceSessionForSnapshot lockForSnapshot(SnapshotVersion snapshotVersion);
    void unlock(PersistenceSessionForSnapshot persistenceSessionForSnapshot);
    void close();
    void beginTransaction();
    void commit();
}
