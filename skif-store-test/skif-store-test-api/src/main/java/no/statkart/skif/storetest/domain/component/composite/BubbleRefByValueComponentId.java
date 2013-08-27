package no.statkart.skif.storetest.domain.component.composite;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class BubbleRefByValueComponentId<T extends BubbleWithCompositeComponent> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleRefByValueComponentId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleRefByValueComponentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public BubbleRefByValueComponentId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (BubbleRefByValueComponentId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public BubbleRefByValueComponentId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (BubbleRefByValueComponentId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public BubbleRefByValueComponentId<? super T> asSnapshotVersionOld() {
        return (BubbleRefByValueComponentId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleRefByValueComponentId<? super T> asSnapshotVersionCurrent() {
        return (BubbleRefByValueComponentId<? super T>) super.asSnapshotVersionCurrent();
    }

}
