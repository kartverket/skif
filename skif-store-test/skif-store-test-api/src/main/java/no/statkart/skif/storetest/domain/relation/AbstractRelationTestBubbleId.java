package no.statkart.skif.storetest.domain.relation;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public abstract class AbstractRelationTestBubbleId<T extends AbstractStoreTestBubble> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public AbstractRelationTestBubbleId(Long value) {
        super(value);
    }

    public AbstractRelationTestBubbleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}