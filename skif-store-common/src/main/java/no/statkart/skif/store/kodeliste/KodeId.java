package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Id klasse for {@link Kode}
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class KodeId<T extends Kode> extends AbstractBubbleId<T> {
    private static final long serialVersionUID = 1L;

    protected KodeId(Object value) {
        super(value);
    }

    protected KodeId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    @SuppressWarnings("unchecked")
    public KodeId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (KodeId<? super T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public KodeId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (KodeId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    @SuppressWarnings("unchecked")
    public KodeId<? super T> asSnapshotVersionCurrent() {
        return (KodeId<? super T>) super.asSnapshotVersionCurrent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public KodeId<? super T> asSnapshotVersionOld() {
        return (KodeId<? super T>) super.asSnapshotVersionOld();
    }

    public abstract KodelisteId<?> getKodelisteId();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}
