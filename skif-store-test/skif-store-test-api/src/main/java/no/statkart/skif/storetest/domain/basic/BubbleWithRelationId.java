package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithRelationId<T extends BubbleWithRelation> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithRelationId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithRelationId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
