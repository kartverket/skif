package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteStringId<T extends KodelisteString> extends KodelisteId<T> {
    public KodelisteStringId(String value) {
        super(value);
    }

    public KodelisteStringId(String value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public String getValue() {
        return (String)super.getValue();
    }

    public KodelisteStringId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (KodelisteStringId<T>)super.asSnapshotVersion(bubbleId);
    }


}
