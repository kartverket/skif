package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.BazId;
import no.statkart.skif.storetest.domain.FooId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BazIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BazId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion snapshotVersion) {
        return new BazId(value, snapshotVersion);
    }
}
