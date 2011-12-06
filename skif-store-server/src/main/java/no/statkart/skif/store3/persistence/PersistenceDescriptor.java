package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersionSeed;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface PersistenceDescriptor<S> {
    int getIndex();
    String getName();
    SnapshotVersionSeed getSeed();
    S getObject();
}
