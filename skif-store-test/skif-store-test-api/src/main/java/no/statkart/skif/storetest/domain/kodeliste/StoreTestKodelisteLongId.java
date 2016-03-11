package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestKodelisteLongId<T extends StoreTestKodelisteLong> extends KodelisteLongId<T> implements StoreTestKodelisteId<T> {

    public StoreTestKodelisteLongId(Long value) {
        super(value);
    }

    @Override
    public StoreTestKodelisteLongId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (StoreTestKodelisteLongId<T>) super.asSnapshotVersion(bubbleId);
    }

    public StoreTestKodelisteLongId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
