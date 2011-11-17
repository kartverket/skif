package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.BazId;

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
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new BazId((Long)value, snapshotVersion);
    }
}
