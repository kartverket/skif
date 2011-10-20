package no.statkart.skif.store;


import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleId<T extends BubbleObject> extends Serializable{
    public Object getValue();
    public SnapshotVersion getSnapshotVersion();
    public BubbleId<T> resolveInstance();
    public T createTypeInstance();
    public Class getBaseType();
    public Class getType();
    public Class getIdValueType();
}
