package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.FilteredBubbleId;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleId;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class FilteredBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return FilteredBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new ParentBubbleId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
