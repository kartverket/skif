package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.tinglysing.NivaaIMatrikkelenhetId;

public class NivaaIMatrikkelenhetIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return NivaaIMatrikkelenhetId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new NivaaIMatrikkelenhetId((Long) value, snapshotTime);
    }
}
