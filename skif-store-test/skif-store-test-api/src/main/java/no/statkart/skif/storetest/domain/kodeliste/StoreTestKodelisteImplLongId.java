package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.KodelisteImplId;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImplLongId<T extends StoreTestKodelisteLongImpl> extends KodelisteImplId<T> implements StoreTestKodelisteLongId<T> {
    public StoreTestKodelisteImplLongId(long value) {
        super(value);
    }

    public StoreTestKodelisteImplLongId(Long value) {
        super(value);
    }

    public StoreTestKodelisteImplLongId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
