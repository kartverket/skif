package no.statkart.skif.storetest2.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest2.domain.eierskap.EiendomId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EiendomIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return EiendomId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new EiendomId((Long)value, snapshotVersion);
    }
}
