package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class TinglysingPaaNyId<T extends TinglysingPaaNy> extends PaategningPaaRettsstiftelserId<T> {
    public TinglysingPaaNyId(Long value) {
        super(value);
    }

    public TinglysingPaaNyId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static TinglysingPaaNyId<?> create(long value) {
        return new TinglysingPaaNyId<TinglysingPaaNy>(new Long(value));
    }
}
