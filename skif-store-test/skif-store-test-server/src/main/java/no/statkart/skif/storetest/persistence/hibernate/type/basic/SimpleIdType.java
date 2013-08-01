package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.mockup.FooId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class SimpleIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return SimpleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new SimpleId((Long)value, snapshotTime);
    }
}
