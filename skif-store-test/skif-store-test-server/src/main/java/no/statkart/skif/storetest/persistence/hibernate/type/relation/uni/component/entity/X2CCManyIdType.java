package no.statkart.skif.storetest.persistence.hibernate.type.relation.uni.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2CCManyId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2CCManyIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return X2CCManyId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new X2CCManyId((Long)value, snapshotTime);
    }
}
