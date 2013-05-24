package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public abstract class StoreTest2KodeId<T extends StoreTest2Kode> extends KodeId<T> implements StoreTest2BubbleId<T> {
    protected StoreTest2KodeId(Object value) {
        super(value);
    }

    protected StoreTest2KodeId(Object value, SnapshotVersion version) {
        super(value, version);
    }
}
