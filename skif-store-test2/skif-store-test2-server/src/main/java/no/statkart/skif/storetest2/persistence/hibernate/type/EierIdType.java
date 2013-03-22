package no.statkart.skif.storetest2.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest2.domain.eierskap.EierId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EierIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return EierId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new EierId((Long)value, snapshotVersion);
    }
}
