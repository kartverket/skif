package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

public class BubbleWithSubtypedEntityComponentId<T extends BubbleWithSubtypedEntityComponent> extends AbstractStoreTestBubbleId<T> {
    public BubbleWithSubtypedEntityComponentId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BubbleWithSubtypedEntityComponentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
