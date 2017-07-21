package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithKodeId<T extends BubbleWithKode> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public Long getValue() {
        return super.getValue();
    }

    @SuppressWarnings("unused")
    public BubbleWithKodeId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public BubbleWithKodeId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (BubbleWithKodeId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public BubbleWithKodeId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (BubbleWithKodeId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public BubbleWithKodeId<? super T> asSnapshotVersionOld() {
        return (BubbleWithKodeId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleWithKodeId<? super T> asSnapshotVersionCurrent() {
        return (BubbleWithKodeId<? super T>) super.asSnapshotVersionCurrent();
    }

}
