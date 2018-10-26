package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Id klasse for {@link AbstractKodeliste} som bruke {@code Object} som idValue type.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class AbstractKodelisteId<T extends AbstractKodeliste> extends AbstractBubbleId<T> implements KodelisteId<T> {
    private static final long serialVersionUID = 1L;

    public AbstractKodelisteId(Object value) {
        super(value);
    }

    public AbstractKodelisteId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractKodelisteId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (AbstractKodelisteId<? super T>) super.asSnapshotVersion(snapshotVersion);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractKodelisteId<? super T> asSnapshotVersionCurrent() {
        return (AbstractKodelisteId<? super T>) super.asSnapshotVersionCurrent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractKodelisteId<? super T> asSnapshotVersionOld() {
        return (AbstractKodelisteId<? super T>) super.asSnapshotVersionOld();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractKodelisteId<? super T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (AbstractKodelisteId<? super T>)super.asSnapshotVersion(bubbleId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}