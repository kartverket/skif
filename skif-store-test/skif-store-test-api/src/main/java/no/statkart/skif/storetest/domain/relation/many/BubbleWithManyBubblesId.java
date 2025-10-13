package no.statkart.skif.storetest.domain.relation.many;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

public class BubbleWithManyBubblesId<T extends BubbleWithManyBubbles> extends AbstractStoreTestBubbleId<T> {
    public BubbleWithManyBubblesId(Long value) {
        super(value);
    }

    public BubbleWithManyBubblesId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
