package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.BubbleWithListId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class BubbleWithListIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithListId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BubbleWithListId((Long) value, snapshotTime);
    }
}
