package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class NedkvitteringId<T extends Nedkvittering> extends PaategningForMatrikkelenheterId<T> {
    public NedkvitteringId(Long value) {
        super(value);
    }

    public NedkvitteringId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static NedkvitteringId<?> create(long value) {
        return new NedkvitteringId<Nedkvittering>(new Long(value));
    }
}
