package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.tinglysing.AndelIMatrikkelenhetId;

public class AndelIMatrikkelenhetIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return AndelIMatrikkelenhetId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new AndelIMatrikkelenhetId((Long) value, snapshotTime);
    }
}
