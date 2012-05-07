package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.tinglysing.MatrikkelenhetId;

public class MatrikkelenhetIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return MatrikkelenhetId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new MatrikkelenhetId((Long) value, snapshotTime);
    }
}
