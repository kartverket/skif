package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.SelfBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SelfBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return SelfBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new SelfBubbleId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
