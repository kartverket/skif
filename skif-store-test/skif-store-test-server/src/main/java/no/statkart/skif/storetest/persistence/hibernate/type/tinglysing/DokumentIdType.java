package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.tinglysing.DokumentId;

public class DokumentIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return DokumentId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new DokumentId((Long) value, snapshotTime);
    }
}
