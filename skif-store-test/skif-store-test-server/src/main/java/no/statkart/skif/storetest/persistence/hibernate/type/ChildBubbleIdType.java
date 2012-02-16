package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.ChildBubbleId;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class ChildBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return ChildBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new ChildBubbleId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
