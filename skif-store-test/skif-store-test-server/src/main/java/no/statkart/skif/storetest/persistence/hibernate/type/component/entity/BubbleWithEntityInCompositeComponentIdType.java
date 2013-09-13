package no.statkart.skif.storetest.persistence.hibernate.type.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponentId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class BubbleWithEntityInCompositeComponentIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithEntityInCompositeComponentId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithEntityInCompositeComponentId((Long)value, snapshotTime);
    }
}
