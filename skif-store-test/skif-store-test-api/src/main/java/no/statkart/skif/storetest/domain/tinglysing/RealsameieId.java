package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class RealsameieId<T extends Realsameie> extends AbstractStoreTestBubbleId<T> {
    public RealsameieId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public RealsameieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static RealsameieId<?> create(long value) {
        return new RealsameieId<Realsameie>(new Long(value));
    }

}
