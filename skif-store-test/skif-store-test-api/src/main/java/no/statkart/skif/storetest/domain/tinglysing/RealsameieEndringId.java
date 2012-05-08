package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class RealsameieEndringId<T extends RealsameieEndring> extends AbstractStoreTestBubbleId<T> {
    public RealsameieEndringId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public RealsameieEndringId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static RealsameieEndringId<?> create(long value) {
        return new RealsameieEndringId<RealsameieEndring>(new Long(value));
    }

}
