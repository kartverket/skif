package no.statkart.skif.storetest2.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest2.domain.subtype.SubTypedBubbleId;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypedBubbleIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return SubTypedBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new SubTypedBubbleId((Long)value, snapshotVersion);
    }
}

