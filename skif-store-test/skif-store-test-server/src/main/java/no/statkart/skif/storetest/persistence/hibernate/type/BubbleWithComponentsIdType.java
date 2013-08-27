package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponentsId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithComponentsIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithComponentsId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithComponentsId((Long) value, snapshotTime);
    }
}
