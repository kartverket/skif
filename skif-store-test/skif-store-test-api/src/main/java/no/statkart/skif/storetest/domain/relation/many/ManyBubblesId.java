package no.statkart.skif.storetest.domain.relation.many;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

public class ManyBubblesId<T extends ManyBubbles>  extends AbstractStoreTestBubbleId<T> {
    public ManyBubblesId(Long value) {
        super(value);
    }

    public ManyBubblesId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
