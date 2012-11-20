package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Id klasse for {@link KodelisteLong} som bruke en {@code Long} som idValue.
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class KodelisteLongId<T extends KodelisteLong> extends AbstractKodelisteId<T> {
    private static final long serialVersionUID = 1L;

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

    @Override
    public KodelisteLongId<T> asSnapshotVersionCurrent() {
        return (KodelisteLongId<T>) super.asSnapshotVersionCurrent();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public KodelisteLongId<T> asSnapshotVersionOld() {
        return (KodelisteLongId<T>) super.asSnapshotVersionOld();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
