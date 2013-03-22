package no.statkart.skif.storetest2.domain;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.2.0
 */

public class AbstractStoreTest2BubbleId<T extends AbstractStoreTest2Bubble> extends AbstractBubbleId<T> implements StoreTest2BubbleId<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public AbstractStoreTest2BubbleId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public AbstractStoreTest2BubbleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
