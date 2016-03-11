package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistoryId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistWithRelationId<T extends HistWithRelation> extends AbstractStoreTestBubbleWithHistoryId<T> {
    private static final long serialVersionUID = 1L;

    public HistWithRelationId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public HistWithRelationId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static HistWithRelationId<HistWithRelation> create(long id, SnapshotVersion snapshotVersion) {
        return new HistWithRelationId<>(id, snapshotVersion);
    }

    @Override
    public HistWithRelationId<T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (HistWithRelationId<T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public HistWithRelationId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (HistWithRelationId<T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public HistWithRelationId<T> asSnapshotVersionOld() {
        return (HistWithRelationId<T>) super.asSnapshotVersionOld();
    }

    @Override
    public BubbleId<? super T> asSnapshotVersionCurrent() {
        return super.asSnapshotVersionCurrent();
    }
}
