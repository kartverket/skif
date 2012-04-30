package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

public class KommuneId<T extends Kommune> extends AbstractStoreTestBubbleId<T> {
    public KommuneId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public KommuneId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static KommuneId<?> create(long value) {
        return new KommuneId<Kommune>(new Long(value));
    }

}
