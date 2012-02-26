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
    /** Brukes av hibernate */
    protected AbstractKodelisteId() {
    }

    public AbstractKodelisteId(Object value) {
        super(value);
    }

    public AbstractKodelisteId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public AbstractKodelisteId<T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (AbstractKodelisteId<T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public AbstractKodelisteId<T> asSnapshotVersionCurrent() {
        return (AbstractKodelisteId<T>) super.asSnapshotVersionCurrent();
    }

    @Override
    public AbstractKodelisteId<T> asSnapshotVersionOld() {
        return (AbstractKodelisteId<T>) super.asSnapshotVersionOld();
    }

    public AbstractKodelisteId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (AbstractKodelisteId<T>)super.asSnapshotVersion(bubbleId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}