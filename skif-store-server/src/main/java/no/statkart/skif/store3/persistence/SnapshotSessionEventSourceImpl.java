package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class SnapshotSessionEventSourceImpl implements SnapshotSessionEventSource{
    private List<SnapshotSessionEventListener> listeners = new ArrayList<SnapshotSessionEventListener>();
    boolean copyOnUpdate = false;
    @Override
    public void addListener(SnapshotSessionEventListener listener) {
        if (copyOnUpdate) {
            listeners = new ArrayList<SnapshotSessionEventListener>(listeners);
            copyOnUpdate = false;
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(SnapshotSessionEventListener listener) {
        if (copyOnUpdate) {
            listeners = new ArrayList<SnapshotSessionEventListener>(listeners);
            copyOnUpdate = false;
        }
        listeners.remove(listener);
    }

    @Override
    public void fireOnClose() {
        copyOnUpdate = true;
        for (SnapshotSessionEventListener listener : listeners) {
            listener.onClose();
        }
        copyOnUpdate = false;
    }

    @Override
    public void fireOnChangeSnapshot() {
        copyOnUpdate = true;
        for (SnapshotSessionEventListener listener : listeners) {
            listener.onChangeSnapshot();
        }
        copyOnUpdate = false;
    }

    @Override
    public void fireOnClear() {
        copyOnUpdate = true;
        for (SnapshotSessionEventListener listener : listeners) {
            listener.onClear();
        }
        copyOnUpdate = false;
    }
}
