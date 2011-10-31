package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteId;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteIdImpl;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodelisteId<T extends StoreTestEnumKodeliste> extends EnumKodelisteIdImpl<T> implements EnumKodelisteId<T>, StoreTestKodelisteId<T> {
    public StoreTestEnumKodelisteId(long value) {
        super(value);
    }

    public StoreTestEnumKodelisteId(Long value) {
        super(value);
    }

    public StoreTestEnumKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
