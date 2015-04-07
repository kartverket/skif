package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.basic.BubbleWithAnyBubbleRefId;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithAnyBubbleRefIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithAnyBubbleRefId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithAnyBubbleRefId((Long)value, snapshotTime);
    }
}
