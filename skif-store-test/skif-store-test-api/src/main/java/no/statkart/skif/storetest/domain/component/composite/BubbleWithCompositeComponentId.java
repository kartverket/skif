package no.statkart.skif.storetest.domain.component.composite;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class BubbleWithCompositeComponentId<T extends BubbleWithCompositeComponent> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithCompositeComponentId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithCompositeComponentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public BubbleWithCompositeComponentId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (BubbleWithCompositeComponentId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public BubbleWithCompositeComponentId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (BubbleWithCompositeComponentId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public BubbleWithCompositeComponentId<? super T> asSnapshotVersionOld() {
        return (BubbleWithCompositeComponentId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleWithCompositeComponentId<? super T> asSnapshotVersionCurrent() {
        return (BubbleWithCompositeComponentId<? super T>) super.asSnapshotVersionCurrent();
    }

}
