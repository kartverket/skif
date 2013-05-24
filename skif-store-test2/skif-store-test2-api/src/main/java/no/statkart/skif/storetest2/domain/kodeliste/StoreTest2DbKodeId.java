package no.statkart.skif.storetest2.domain.kodeliste;


import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 2.2.1
 */
public class StoreTest2DbKodeId<T extends StoreTest2DbKode> extends StoreTest2KodeId<T> {
    protected StoreTest2DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public KodelisteId<?> getKodelisteId() {
        throw new NotImplementedException();
    }
}
