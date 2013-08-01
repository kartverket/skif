package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.mockup.FooId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistSimpleIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return  HistSimpleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new  HistSimpleId((Long)value, snapshotTime);
    }
}
