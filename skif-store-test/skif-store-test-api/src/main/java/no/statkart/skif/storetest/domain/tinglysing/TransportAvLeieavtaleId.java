package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class TransportAvLeieavtaleId<T extends TransportAvLeieavtale> extends TransportPaategningId<T> {
    public TransportAvLeieavtaleId(Long value) {
        super(value);
    }

    public TransportAvLeieavtaleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static TransportAvLeieavtaleId<?> create(long value) {
        return new TransportAvLeieavtaleId<TransportAvLeieavtale>(new Long(value));
    }
}
