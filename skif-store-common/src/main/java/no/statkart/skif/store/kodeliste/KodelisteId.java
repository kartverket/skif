package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Interface for Id klassen hørende til {@link Kodeliste}.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KodelisteId<T extends Kodeliste> extends BubbleId<T> {
    @Override
    KodelisteId<? super T> asSnapshotVersion(BubbleId<?> bubbleId);

    @Override
    KodelisteId<? super T> asSnapshotVersion(SnapshotVersion snapshotVersion);

    @Override
    KodelisteId<? super T> asSnapshotVersionCurrent();

    @Override
    KodelisteId<? super T> asSnapshotVersionOld();
}
