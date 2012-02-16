package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.ParrentBubbleId;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class ParrentBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return ParrentBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new ParrentBubbleId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
