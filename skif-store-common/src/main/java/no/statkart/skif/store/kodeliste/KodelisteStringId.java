package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Id klasse for {@link KodelisteString} som bruke en {@code String} som idValue.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteStringId<T extends KodelisteString> extends AbstractKodelisteId<T> {
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

    @Override
    public KodelisteStringId<T> asSnapshotVersion(BubbleId<?> bubbleId) {
        return (KodelisteStringId<T>) super.asSnapshotVersion(bubbleId);
    }

    @Override
    public KodelisteStringId<T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        return (KodelisteStringId<T>) super.asSnapshotVersion(snapshotVersion);
    }

    @Override
    public KodelisteStringId<T> asSnapshotVersionCurrent() {
        return (KodelisteStringId<T>) super.asSnapshotVersionCurrent();
    }

    @Override
    public KodelisteStringId<T> asSnapshotVersionOld() {
        return (KodelisteStringId<T>) super.asSnapshotVersionOld();
    }
}
