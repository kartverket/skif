package no.statkart.skif.storetest.history;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */

public class FooId<T extends Foo> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public FooId(Object value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public FooId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
