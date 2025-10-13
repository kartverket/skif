package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

public class BubbleWithSubtypedEntityComponentSetId<T extends BubbleWithSubtypedEntityComponentSet> extends AbstractStoreTestBubbleId<T> {
    public BubbleWithSubtypedEntityComponentSetId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithSubtypedEntityComponentSetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
