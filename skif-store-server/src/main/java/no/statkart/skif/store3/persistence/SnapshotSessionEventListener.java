package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public interface SnapshotSessionEventListener {
    void onChangeSnapshot();
    void onClear();
    void onClose();
}
