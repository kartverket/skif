package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Henrik Fredholm
 */
public class PersistenceDescriptorWithStack<S, W extends PersistenceDescriptor<?>> extends PersistenceDescriptorWrapper<S, W> {
    Deque<SnapshotVersion> stack = new ArrayDeque<SnapshotVersion>();

    public PersistenceDescriptorWithStack(W wrapped) {
        super(wrapped);
    }

    public void pushSnapshotVersion(SnapshotVersion snapshotVersion) {
        stack.push(wrapped.getSeed().get());
        wrapped.getSeed().set(snapshotVersion);
    }

    public SnapshotVersion popSnapshotVersion() {
        SnapshotVersion snapshotVersion = stack.pop();
        wrapped.getSeed().set(snapshotVersion);
        return snapshotVersion;
    }

    public SnapshotVersion peekSnapshotVersion() {
        return stack.pop();
    }

}
