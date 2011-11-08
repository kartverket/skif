package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImplLongId<T extends StoreTestKodelisteImplLong> extends KodelisteId<T> implements StoreTestKodelisteLongId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();    //To change body of overridden methods use File | Settings | File Templates.
    }

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
