package no.statkart.skif.storetest.persistence.hibernate.type.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithEntityComponentIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithEntityComponentId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithEntityComponentId((Long)value, snapshotTime);
    }
}
