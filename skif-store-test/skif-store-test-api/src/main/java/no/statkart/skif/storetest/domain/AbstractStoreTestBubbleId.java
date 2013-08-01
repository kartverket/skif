package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */

public class AbstractStoreTestBubbleId<T extends AbstractStoreTestBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public AbstractStoreTestBubbleId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public AbstractStoreTestBubbleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
