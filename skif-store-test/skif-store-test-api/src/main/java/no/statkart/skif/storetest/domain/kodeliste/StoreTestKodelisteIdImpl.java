package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.KodelisteIdImpl;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteIdImpl<T extends StoreTestKodelisteImpl> extends KodelisteIdImpl<T> implements StoreTestKodelisteId<T> {
    public StoreTestKodelisteIdImpl(long value) {
        super(value);
    }

    public StoreTestKodelisteIdImpl(Long value) {
        super(value);
    }

    public StoreTestKodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
