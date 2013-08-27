package no.statkart.skif.storetest.persistence.hibernate.type.endring;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.endringslogg.EndringId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return EndringId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new EndringId((Long)value, snapshotVersion);
    }
}
