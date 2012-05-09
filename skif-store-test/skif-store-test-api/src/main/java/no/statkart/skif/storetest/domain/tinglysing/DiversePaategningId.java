package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class DiversePaategningId<T extends DiversePaategning> extends PaategningForMatrikkelenheterId<T> {
    public DiversePaategningId(Long value) {
        super(value);
    }

    public DiversePaategningId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static DiversePaategningId<?> create(long value) {
        return new DiversePaategningId<DiversePaategning>(new Long(value));
    }
}
