package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface SnapshotManagedPersistenceSession<S extends PersistenceSession> {
    S acquireForSnapshot(SnapshotVersion snapshotVersion);
    S releaseForSnapshot(PersistenceSession s);
    PersistenceDescriptor[] getPersistenceDescriptors();
}
