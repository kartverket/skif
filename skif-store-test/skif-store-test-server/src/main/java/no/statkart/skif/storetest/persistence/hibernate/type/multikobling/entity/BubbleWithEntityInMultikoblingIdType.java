package no.statkart.skif.storetest.persistence.hibernate.type.multikobling.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.multikobling.entity.BubbleWithEntityInMultikoblingId;

/**
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public class BubbleWithEntityInMultikoblingIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithEntityInMultikoblingId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new BubbleWithEntityInMultikoblingId((Long)value, snapshotVersion);
    }
}
