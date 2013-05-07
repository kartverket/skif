package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link StoreTest2EnumKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public abstract class StoreTest2EnumKodeId<T extends StoreTest2EnumKode> extends StoreTest2KodeId<T> {
    protected StoreTest2EnumKodeId(Object value) {
        super(value);
    }

    protected StoreTest2EnumKodeId(Object value, SnapshotVersion version) {
        super(value, version);
    }
}
