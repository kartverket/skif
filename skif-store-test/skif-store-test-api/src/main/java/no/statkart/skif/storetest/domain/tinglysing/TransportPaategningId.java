package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class TransportPaategningId<T extends TransportPaategning> extends PaategningPaaRettsstiftelserId<T> {
    public TransportPaategningId(Long value) {
        super(value);
    }

    public TransportPaategningId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static TransportPaategningId<?> create(long value) {
        return new TransportPaategningId<TransportPaategning>(new Long(value));
    }
}
