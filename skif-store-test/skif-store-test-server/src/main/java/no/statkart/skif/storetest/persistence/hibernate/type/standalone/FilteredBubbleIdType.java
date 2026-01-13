package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.FilteredBubbleId;

/**
 * @author Jan Holmen
 * @since 2.1
 */
public class FilteredBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return FilteredBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new FilteredBubbleId<>((Long) value, snapshotVersion);
    }

}
