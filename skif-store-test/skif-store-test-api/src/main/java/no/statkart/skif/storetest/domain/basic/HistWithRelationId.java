package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistoryId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistWithRelationId<T extends HistWithRelation> extends AbstractStoreTestBubbleWithHistoryId<T> {
    private static final long serialVersionUID = 1L;

    public Long getValue() {
        return (Long) super.getValue();
    }

    public HistWithRelationId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public HistWithRelationId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
