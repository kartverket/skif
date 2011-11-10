package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodelisteId;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodelisteLongId<T extends StoreTestEnumKodelisteLong> extends EnumKodelisteId<T> implements StoreTestKodelisteLongId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public StoreTestEnumKodelisteLongId(long value) {
        super(new Long(value));
    }

    public StoreTestEnumKodelisteLongId(Long value) {
        super(value);
    }

    public StoreTestEnumKodelisteLongId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
