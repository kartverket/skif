package no.statkart.skif.storetest.persistence.hibernate.type.component.composite;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithCompositeComponentIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithCompositeComponentId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithCompositeComponentId((Long)value, snapshotTime);
    }
}
