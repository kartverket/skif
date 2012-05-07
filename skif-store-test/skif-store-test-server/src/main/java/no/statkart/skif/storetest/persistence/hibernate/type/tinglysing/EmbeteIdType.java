package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.tinglysing.EmbeteId;

/**
 * @author rorchr
 */
public class EmbeteIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return EmbeteId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new EmbeteId((Long)value, snapshotTime);
    }
}
