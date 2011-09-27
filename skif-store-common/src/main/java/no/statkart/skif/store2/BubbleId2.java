package no.statkart.skif.store2;


import no.statkart.skif.store.SnapshotVersion;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleId2<T extends BubbleObject2> extends Serializable{
    public Object getValue();
    public SnapshotVersion getReplicaVersion();
    public BubbleId2<T> resolveInstance();
    public T createTypeInstance();
    public Class getBaseType();
    public Class getType();
}
