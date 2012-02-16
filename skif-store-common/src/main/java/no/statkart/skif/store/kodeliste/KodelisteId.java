package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class KodelisteId<T extends Kodeliste> extends AbstractBubbleId<T> {
    protected KodelisteId() {
    }

    public KodelisteId(Object value) {
        super(value);
    }

    public KodelisteId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    public KodelisteId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (KodelisteId<T>)super.asSnapshotVersion(bubbleId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}