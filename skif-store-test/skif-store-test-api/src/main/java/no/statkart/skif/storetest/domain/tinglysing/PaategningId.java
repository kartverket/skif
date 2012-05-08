package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class PaategningId<T extends Paategning> extends AbstractStoreTestBubbleId<T> {
    public PaategningId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public PaategningId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PaategningId<?> create(long value) {
        return new PaategningId<Paategning>(new Long(value));
    }

}
