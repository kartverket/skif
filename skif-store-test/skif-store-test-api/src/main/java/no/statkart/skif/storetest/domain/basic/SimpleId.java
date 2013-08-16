package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.mockup.Foo;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class SimpleId<T extends Simple> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public SimpleId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public SimpleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public SimpleId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (SimpleId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public SimpleId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (SimpleId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public SimpleId<? super T> asSnapshotVersionOld() {
        return (SimpleId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public SimpleId<? super T> asSnapshotVersionCurrent() {
        return (SimpleId<? super T>) super.asSnapshotVersionCurrent();
    }

}
