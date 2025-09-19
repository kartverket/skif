package no.statkart.skif.storetest.persistence.hibernate.type.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentId;

/**
 * @author Martin Halleland
 * @since 4.6
 */
public class BubbleWithSubtypedEntityComponentIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithSubtypedEntityComponentId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithSubtypedEntityComponentId((Long) value, snapshotTime);
    }
}
