package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class FooId <T extends Foo> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    public FooId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public FooId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
