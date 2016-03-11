package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistWithRelationIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return HistWithRelationId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new HistWithRelationId((Long)value, snapshotTime);
    }
}
