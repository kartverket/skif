package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class StoreTestDbKodeId<T extends StoreTestDbKode> extends StoreTestKodeId<T> {
    protected StoreTestDbKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
