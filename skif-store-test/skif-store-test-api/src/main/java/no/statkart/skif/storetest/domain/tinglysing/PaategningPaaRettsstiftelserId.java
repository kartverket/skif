package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PaategningPaaRettsstiftelserId<T extends PaategningPaaRettsstiftelser> extends RettsstiftelseId<T> {
    public PaategningPaaRettsstiftelserId(Long value) {
        super(value);
    }

    public PaategningPaaRettsstiftelserId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PaategningPaaRettsstiftelserId<?> create(long value) {
        return new PaategningPaaRettsstiftelserId<PaategningPaaRettsstiftelser>(new Long(value));
    }
}
