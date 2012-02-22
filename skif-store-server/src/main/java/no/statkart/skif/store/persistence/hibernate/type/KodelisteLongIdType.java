package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteLongId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class KodelisteLongIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return KodelisteLongId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new KodelisteLongId((Long) value, snapshotVersion);
    }
}
