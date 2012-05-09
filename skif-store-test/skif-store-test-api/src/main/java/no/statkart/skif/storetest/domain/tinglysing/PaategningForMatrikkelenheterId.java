package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PaategningForMatrikkelenheterId<T extends PaategningForMatrikkelenheter> extends RettsstiftelseId<T> {
    public PaategningForMatrikkelenheterId(Long value) {
        super(value);
    }

    public PaategningForMatrikkelenheterId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PaategningForMatrikkelenheterId<?> create(long value) {
        return new PaategningForMatrikkelenheterId<PaategningForMatrikkelenheter>(new Long(value));
    }
}
