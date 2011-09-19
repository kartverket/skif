package no.statkart.skif.store2;

import no.statkart.skif.store.ReplicaVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleId2<T extends BubbleObject2> {
    public Object getValue();
    public ReplicaVersion getReplicaVersion();
    public BubbleId2<T> resolveInstance();
    public T createTypeInstance();
}
