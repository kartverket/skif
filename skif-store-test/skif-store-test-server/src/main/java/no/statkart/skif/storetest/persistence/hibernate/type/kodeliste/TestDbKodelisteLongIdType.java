package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestDbKodelisteLongIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return StoreTestDbKodelisteLongId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion snapshotVersion) {
        return new StoreTestDbKodelisteLongId(value, snapshotVersion);
    }        
}


