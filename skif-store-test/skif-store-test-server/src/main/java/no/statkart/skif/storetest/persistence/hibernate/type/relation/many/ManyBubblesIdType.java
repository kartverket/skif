package no.statkart.skif.storetest.persistence.hibernate.type.relation.many;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.relation.many.ManyBubblesId;

public class ManyBubblesIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return ManyBubblesId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new ManyBubblesId<>((Long)value, snapshotTime);
    }
}
