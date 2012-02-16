package no.statkart.skif.storetest.domain.kode;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodeId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public abstract class StoreTestEnumKodeId<T extends StoreTestEnumKode> extends EnumKodeId<T> implements StoreTestKodeId<T> {
    protected StoreTestEnumKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
