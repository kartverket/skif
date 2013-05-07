package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * Id for {@link StoreTest2Kode}.
 */
public abstract class StoreTest2KodeId<T extends StoreTest2Kode> extends KodeId<T> implements StoreTest2BubbleId<T> {
    protected StoreTest2KodeId(Object value) {
        super(value);
    }

    protected StoreTest2KodeId(Object value, SnapshotVersion version) {
        super(value, version);
    }
}
