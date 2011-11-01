package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.KodelisteImplId;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImplId<T extends StoreTestKodelisteImpl> extends KodelisteImplId<T> implements StoreTestKodelisteId<T> {
    public StoreTestKodelisteImplId(long value) {
        super(value);
    }

    public StoreTestKodelisteImplId(Long value) {
        super(value);
    }

    public StoreTestKodelisteImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
