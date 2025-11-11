package no.statkart.skif.store;


import no.statkart.skif.store.module.common.BubbleIdFactory;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleId<T extends BubbleObject> extends Serializable, Comparable<Object> {

    Object getValue();
    @SuppressWarnings("unchecked")
    default <S> S createIdOfSubtype(Class<S> idClass) {
        return (S) BubbleIdFactory.createInstance((Class<? extends BubbleId>)idClass, getValue(), getSnapshotVersion());
    }

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
