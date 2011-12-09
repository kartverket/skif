package no.statkart.skif.storetest.domain.nonhist;

import no.statkart.skif.store.AbstractNonVersionedBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */

public class FooId<T extends Foo> extends AbstractNonVersionedBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public FooId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public FooId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
