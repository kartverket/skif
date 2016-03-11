package no.statkart.skif.store;


import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleId<T extends BubbleObject> extends Serializable, Comparable<Object>{

    Object getValue();

    SnapshotVersion getSnapshotVersion();

    T createTypeInstance();

    Class getBaseType();

    Class<T> getType();

    Class getValueType();

    Class<? extends BubbleId<? super T>> getBaseIdType();

    boolean equalsIgnoreSnapshotVersion(Object object);

    BubbleId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion);

    BubbleId<? super T> asSnapshotVersion(BubbleId<?> bubbleId);

    BubbleId<? super T> asSnapshotVersionOld();

    BubbleId<? super T> asSnapshotVersionCurrent();

    BubbleId<? super T> asBase();

}
