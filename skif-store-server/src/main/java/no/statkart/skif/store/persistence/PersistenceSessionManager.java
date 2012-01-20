package no.statkart.skif.store.persistence;

import no.statkart.skif.persistence.TransactionalResource;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionManager extends PersistenceSession, TransactionalResource {
    PersistenceSessionForSnapshot getForSnapshotVersion(SnapshotVersion snapshotVersion);
    PersistenceSessionForSnapshot lockForSnapshot(SnapshotVersion snapshotVersion);
    void unlock(PersistenceSessionForSnapshot persistenceSessionForSnapshot);
}
