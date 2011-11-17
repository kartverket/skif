package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImplStringId<T extends StoreTestKodelisteImplString> extends KodelisteId<T> implements StoreTestKodelisteStringId<T> {

    public StoreTestKodelisteImplStringId(String value) {
        super(value);
    }

    public StoreTestKodelisteImplStringId(String value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
