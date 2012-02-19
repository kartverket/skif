package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public abstract class StoreTestKodeId<T extends StoreTestKode> extends KodeId<T> implements StoreTestBubbleId<T> {
    protected StoreTestKodeId(Object value) {
        super(value);
    }

    protected StoreTestKodeId(Object value, SnapshotVersion version) {
        super(value, version);
    }
}
