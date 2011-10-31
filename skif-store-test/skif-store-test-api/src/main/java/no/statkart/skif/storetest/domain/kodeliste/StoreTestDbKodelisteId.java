package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodelisteId;
import no.statkart.skif.store.kodelistesupport.DbKodelisteIdImpl;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class StoreTestDbKodelisteId<T extends StoreTestDbKodeliste> extends DbKodelisteIdImpl<T> implements DbKodelisteId<T>, StoreTestKodelisteId<T> {

    public StoreTestDbKodelisteId(long value) {
        super(value);
    }

    public StoreTestDbKodelisteId(Long value) {
        super(value);
    }
    public StoreTestDbKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
