package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class SlettingId<T extends Sletting> extends PaategningPaaRettsstiftelserId<T> {
    public SlettingId(Long value) {
        super(value);
    }

    public SlettingId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static SlettingId<?> create(long value) {
        return new SlettingId<Sletting>(new Long(value));
    }
}
