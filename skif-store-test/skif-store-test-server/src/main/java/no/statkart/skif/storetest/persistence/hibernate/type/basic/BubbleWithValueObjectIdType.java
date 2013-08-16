package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObjectId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithValueObjectIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithValueObjectId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithValueObjectId((Long)value, snapshotTime);
    }
}
