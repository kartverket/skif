package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodelisteId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class StoreTestDbKodelisteLongId<T extends StoreTestDbKodelisteLong> extends DbKodelisteId<T> implements StoreTestKodelisteLongId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public StoreTestDbKodelisteLongId(long value) {
        super(new Long(value));
    }

    public StoreTestDbKodelisteLongId(Long value) {
        super(value);
    }
    public StoreTestDbKodelisteLongId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
