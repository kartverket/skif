package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.FilteredBubbleId;
import no.statkart.skif.storetest.domain.demo.ParrentBubbleId;

/**
 * @author Jan Holmen
 * @since 2.0
 */
public class FilteredBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return FilteredBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new ParrentBubbleId((Long)value).asReplicaVersion(snapshotVersion);
    }

}
