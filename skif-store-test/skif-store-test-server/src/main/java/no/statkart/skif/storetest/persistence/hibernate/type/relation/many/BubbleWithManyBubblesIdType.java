package no.statkart.skif.storetest.persistence.hibernate.type.relation.many;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.relation.many.BubbleWithManyBubblesId;

public class BubbleWithManyBubblesIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithManyBubblesId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithManyBubblesId<>((Long)value, snapshotTime);
    }
}
