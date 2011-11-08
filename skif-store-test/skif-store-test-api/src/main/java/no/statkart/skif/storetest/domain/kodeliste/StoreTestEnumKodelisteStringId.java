package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodelisteId;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodelisteStringId<T extends StoreTestEnumKodelisteString> extends EnumKodelisteId<T> implements StoreTestKodelisteStringId<T> {

    @Override
    public String getValue() {
        return (String)super.getValue();
    }

    public StoreTestEnumKodelisteStringId(String value) {
        super(value);
    }

    public StoreTestEnumKodelisteStringId(String value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
