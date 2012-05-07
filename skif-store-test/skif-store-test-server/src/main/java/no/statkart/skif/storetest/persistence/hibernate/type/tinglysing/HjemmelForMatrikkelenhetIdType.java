package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.tinglysing.HjemmelForMatrikkelenhetId;

public class HjemmelForMatrikkelenhetIdType extends RettsstiftelseIdType {
    @Override
    public Class returnedClass() {
        return HjemmelForMatrikkelenhetId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new HjemmelForMatrikkelenhetId((Long)value, snapshotTime);
    }
}
