package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */

public class BubbleWithEntityInCompositeComponentId<T extends BubbleWithEntityInCompositeComponent> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithEntityInCompositeComponentId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithEntityInCompositeComponentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public BubbleWithEntityInCompositeComponentId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (BubbleWithEntityInCompositeComponentId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public BubbleWithEntityInCompositeComponentId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (BubbleWithEntityInCompositeComponentId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public BubbleWithEntityInCompositeComponentId<? super T> asSnapshotVersionOld() {
        return (BubbleWithEntityInCompositeComponentId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleWithEntityInCompositeComponentId<? super T> asSnapshotVersionCurrent() {
        return (BubbleWithEntityInCompositeComponentId<? super T>) super.asSnapshotVersionCurrent();
    }

}
