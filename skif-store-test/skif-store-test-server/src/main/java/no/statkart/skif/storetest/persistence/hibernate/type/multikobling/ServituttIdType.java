package no.statkart.skif.storetest.persistence.hibernate.type.multikobling;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.multikobling.RettsstiftelseId;
import no.statkart.skif.storetest.domain.multikobling.ServituttId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ServituttIdType extends RettsstiftelseIdType {
    @Override
    public Class returnedClass() {
        return ServituttId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new ServituttId((Long)value, snapshotTime);
    }
}
