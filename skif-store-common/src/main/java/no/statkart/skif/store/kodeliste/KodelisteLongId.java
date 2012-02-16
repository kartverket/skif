package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteLongId<T extends KodelisteLong> extends KodelisteId<T> {
    public KodelisteLongId(Long value) {
        super(value);
    }

    public KodelisteLongId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public KodelisteLongId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (KodelisteLongId<T>)super.asSnapshotVersion(bubbleId);
    }


}
