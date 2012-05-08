package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class HjemmelsovergangId<T extends Hjemmelsovergang> extends AbstractStoreTestBubbleId<T> {
    public HjemmelsovergangId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public HjemmelsovergangId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static HjemmelsovergangId<?> create(long value) {
        return new HjemmelsovergangId<Hjemmelsovergang>(new Long(value));
    }

}
