package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class TransportAvFesteId<T extends TransportAvFeste> extends AbstractStoreTestBubbleId<T> {
    public TransportAvFesteId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public TransportAvFesteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static TransportAvFesteId<?> create(long value) {
        return new TransportAvFesteId<TransportAvFeste>(new Long(value));
    }
}