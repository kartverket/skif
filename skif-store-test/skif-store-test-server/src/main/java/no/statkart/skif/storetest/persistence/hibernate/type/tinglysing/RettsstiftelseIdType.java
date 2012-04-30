package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.multikobling.RettsstiftelseId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RettsstiftelseIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return RettsstiftelseId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new RettsstiftelseId((Long)value, snapshotTime);
    }
}
