package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public abstract class StoreTestEnumKodeId<T extends StoreTestEnumKode> extends StoreTestKodeId<T> {
    protected StoreTestEnumKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
