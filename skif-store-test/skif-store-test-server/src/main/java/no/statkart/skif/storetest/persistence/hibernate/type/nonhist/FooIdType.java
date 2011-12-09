package no.statkart.skif.storetest.persistence.hibernate.type.nonhist;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.nonhist.FooId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class FooIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return FooId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new FooId((Long)value, snapshotTime);
    }
}
