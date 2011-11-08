package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodelisteId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class StoreTestDbKodelisteStringId<T extends StoreTestDbKodelisteString> extends DbKodelisteId<T> implements StoreTestKodelisteStringId<T> {

    @Override
    public String getValue() {
        return (String)super.getValue();
    }


    public StoreTestDbKodelisteStringId(String value) {
        super(value);
    }
    public StoreTestDbKodelisteStringId(String value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
