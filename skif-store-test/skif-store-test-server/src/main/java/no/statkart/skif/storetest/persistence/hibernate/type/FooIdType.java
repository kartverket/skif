package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.FooId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class FooIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return FooId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion snapshotTime) {
        return new FooId(value, snapshotTime);
    }
}
