package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class PantefrafallId<T extends Pantefrafall> extends AbstractStoreTestBubbleId<T> {
    public PantefrafallId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public PantefrafallId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PantefrafallId<?> create(long value) {
        return new PantefrafallId<Pantefrafall>(new Long(value));
    }

}
