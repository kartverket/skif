package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface SnapshotSessionEventSource {
    void addListener(SnapshotSessionEventListener listener);
    void removeListener (SnapshotSessionEventListener listener);
    void fireOnClose();
    void fireOnChangeSnapshot();
    void fireOnClear();
}
