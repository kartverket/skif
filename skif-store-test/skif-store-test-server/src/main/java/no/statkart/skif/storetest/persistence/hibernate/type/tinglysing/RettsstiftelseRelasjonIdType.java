package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.tinglysing.RettsstiftelseRelasjonId;

/**
 * @author rorchr
 */
public class RettsstiftelseRelasjonIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return RettsstiftelseRelasjonId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new RettsstiftelseRelasjonId((Long)value, snapshotTime);
    }
}
