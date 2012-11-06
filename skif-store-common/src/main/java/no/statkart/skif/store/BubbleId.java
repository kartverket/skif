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
    public Class<T> getType();
    public Class getValueType();
    public boolean equalsIgnoreSnapshotVersion(Object object);
    public BubbleId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion);
    public BubbleId<? super T> asSnapshotVersion(BubbleId<?>  bubbleId);
    public BubbleId<? super T> asSnapshotVersionOld();
    public BubbleId<? super T> asSnapshotVersionCurrent();
}
