package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class HjemmelId<T extends Hjemmel> extends AbstractStoreTestBubbleId<T> {
    public HjemmelId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public HjemmelId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static HjemmelId<?> create(long value) {
        return new HjemmelId<Hjemmel>(new Long(value));
    }

}
