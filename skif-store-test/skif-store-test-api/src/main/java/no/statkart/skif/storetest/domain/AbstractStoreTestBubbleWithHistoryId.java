package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */

public abstract class AbstractStoreTestBubbleWithHistoryId<T extends AbstractStoreTestBubbleWithHistory> extends AbstractStoreTestBubbleId<T>{
    private static final long serialVersionUID = 1L;

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public AbstractStoreTestBubbleWithHistoryId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public AbstractStoreTestBubbleWithHistoryId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
