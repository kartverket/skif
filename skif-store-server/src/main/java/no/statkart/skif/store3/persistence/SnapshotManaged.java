package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface SnapshotManaged<S> {
    S acquireForSnapshot(SnapshotVersion snapshotVersion);
    S releaseForSnapshot(S s);
    PersistenceDescriptor[] getPersistenceDescriptors();
    void close();
}
