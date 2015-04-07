package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class BubbleWithAnyBubbleRefId<T extends BubbleWithAnyBubbleRef> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithAnyBubbleRefId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithAnyBubbleRefId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public BubbleWithAnyBubbleRefId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (BubbleWithAnyBubbleRefId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public BubbleWithAnyBubbleRefId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (BubbleWithAnyBubbleRefId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public BubbleWithAnyBubbleRefId<? super T> asSnapshotVersionOld() {
        return (BubbleWithAnyBubbleRefId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleWithAnyBubbleRefId<? super T> asSnapshotVersionCurrent() {
        return (BubbleWithAnyBubbleRefId<? super T>) super.asSnapshotVersionCurrent();
    }

}
