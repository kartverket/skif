package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.2.1
 */
public class StoreTest2KodelisteLongId<T extends StoreTest2KodelisteLong> extends KodelisteLongId<T> implements StoreTest2KodelisteId<T> {

    public StoreTest2KodelisteLongId(Long value) {
        super(value);
    }

    @Override
    public StoreTest2KodelisteLongId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (StoreTest2KodelisteLongId<T>) super.asSnapshotVersion(bubbleId);
    }

    public StoreTest2KodelisteLongId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
