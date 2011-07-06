package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleId<T extends BubbleObject> extends Serializable {
    public Object getValue();
    public ReplicaVersion getReplicaVersion();
    public BubbleId<T> resolveInstance();
    public T createTypeInstance();
}
