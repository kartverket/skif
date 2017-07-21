package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class BubbleWithEntityComponentId<T extends BubbleWithEntityComponent> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public BubbleWithEntityComponentId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithEntityComponentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public BubbleWithEntityComponentId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (BubbleWithEntityComponentId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public BubbleWithEntityComponentId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (BubbleWithEntityComponentId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public BubbleWithEntityComponentId<? super T> asSnapshotVersionOld() {
        return (BubbleWithEntityComponentId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleWithEntityComponentId<? super T> asSnapshotVersionCurrent() {
        return (BubbleWithEntityComponentId<? super T>) super.asSnapshotVersionCurrent();
    }

}
