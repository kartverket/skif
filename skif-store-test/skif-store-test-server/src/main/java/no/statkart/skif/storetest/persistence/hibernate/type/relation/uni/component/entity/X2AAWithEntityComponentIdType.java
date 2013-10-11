package no.statkart.skif.storetest.persistence.hibernate.type.relation.uni.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2AAWithEntityComponentIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return X2AAWithEntityComponentId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new X2AAWithEntityComponentId((Long)value, snapshotTime);
    }
}
