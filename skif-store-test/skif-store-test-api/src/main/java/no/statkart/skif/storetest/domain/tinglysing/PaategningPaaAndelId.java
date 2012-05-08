package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class PaategningPaaAndelId<T extends PaategningPaaAndel> extends AbstractStoreTestBubbleId<T> {
    public PaategningPaaAndelId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public PaategningPaaAndelId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PaategningPaaAndelId<?> create(long value) {
        return new PaategningPaaAndelId<PaategningPaaAndel>(new Long(value));
    }

}
