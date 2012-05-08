package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class JordsameieId<T extends Jordsameie> extends AbstractStoreTestBubbleId<T> {
    public JordsameieId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public JordsameieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static JordsameieId<?> create(long value) {
        return new JordsameieId<Jordsameie>(new Long(value));
    }

}