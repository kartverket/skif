package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.SnapshotVersion;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Henrik Fredholm
 */
public class PersistenceDescriptorWithStack<S, W extends PersistenceDescriptor<?>> extends PersistenceDescriptorWrapper<S, W> {
    private Deque<SnapshotVersion> stack = new ArrayDeque<SnapshotVersion>();

    public PersistenceDescriptorWithStack(W wrapped) {
        super(wrapped);
    }

    public void pushSnapshotVersion(SnapshotVersion snapshotVersion) {
        stack.push(snapshotVersion);
    }

    public SnapshotVersion popSnapshotVersion() {
        return stack.pop();
    }

    public SnapshotVersion peekSnapshotVersion() {
        return stack.peek();
    }

}
