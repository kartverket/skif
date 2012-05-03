package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.multikobling.ServituttId;
import no.statkart.skif.storetest.domain.tinglysing.HjemmelForPersonId;

public class HjemmelsinformasjonIdType extends RettsstiftelseIdType {
    @Override
    public Class returnedClass() {
        return ServituttId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new HjemmelForPersonId((Long)value, snapshotTime);
    }
}
