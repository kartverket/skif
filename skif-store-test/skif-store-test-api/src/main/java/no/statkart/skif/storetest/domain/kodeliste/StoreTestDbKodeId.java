package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodeId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class StoreTestDbKodeId<T extends StoreTestDbKode> extends DbKodeId<T> implements StoreTestKodeId<T> {
    protected StoreTestDbKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
