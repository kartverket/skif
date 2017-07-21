package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteStringId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestKodelisteStringId<T extends StoreTestKodelisteString> extends KodelisteStringId<T> implements StoreTestKodelisteId<T> {

    public StoreTestKodelisteStringId(String value) {
        super(value);
    }

    @Override
    public StoreTestKodelisteStringId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (StoreTestKodelisteStringId<T>) super.asSnapshotVersion(bubbleId);
    }

    @SuppressWarnings("unused")
    public StoreTestKodelisteStringId(String value, SnapshotVersion version) {
        super(value, version);
    }
}
