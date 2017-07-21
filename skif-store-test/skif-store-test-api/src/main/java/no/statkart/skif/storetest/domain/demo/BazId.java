package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractNonVersionedBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class BazId<T extends Baz> extends AbstractNonVersionedBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    @SuppressWarnings("unused")
    public BazId(Long value) {
        super(value);
    }

    public BazId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
