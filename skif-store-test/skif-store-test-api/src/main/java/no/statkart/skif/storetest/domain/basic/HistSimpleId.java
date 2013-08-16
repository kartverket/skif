package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistoryId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class HistSimpleId<T extends HistSimple> extends AbstractStoreTestBubbleWithHistoryId<T> {
    private static final long serialVersionUID = 1L;

    public HistSimpleId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public HistSimpleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public HistSimpleId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (HistSimpleId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public HistSimpleId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (HistSimpleId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public HistSimpleId<? super T> asSnapshotVersionOld() {
        return (HistSimpleId<? super T>) super.asSnapshotVersionOld();
    }

    @Override
    public HistSimpleId<? super T> asSnapshotVersionCurrent() {
        return (HistSimpleId<? super T>) super.asSnapshotVersionCurrent();
    }

    public static HistSimpleId<HistSimple> create(long id, SnapshotVersion snapshotVersion) {
        return new HistSimpleId<HistSimple>(id, snapshotVersion);
    }

}
