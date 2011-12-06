package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface SnapshotSessionEventListener {
    void onChangeSnapshot(SnapshotVersion snapshotVersion);
    void onFlush();
    void onClear();
    void onEvict();
}
