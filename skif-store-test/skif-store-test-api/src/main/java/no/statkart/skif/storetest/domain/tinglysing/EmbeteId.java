package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

public class EmbeteId<T extends Embete> extends AbstractStoreTestBubbleId<T> {
    public EmbeteId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public EmbeteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static EmbeteId<?> create(long value) {
        return new EmbeteId<Embete>(new Long(value));
    }

}
