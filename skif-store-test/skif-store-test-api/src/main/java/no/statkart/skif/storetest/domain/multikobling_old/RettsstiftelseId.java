package no.statkart.skif.storetest.domain.multikobling_old;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RettsstiftelseId<T extends Rettsstiftelse> extends AbstractStoreTestBubbleId<T> {
    public RettsstiftelseId(Long value) {
        super(value);
    }

    public RettsstiftelseId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static RettsstiftelseId<?> create(long value) {
        return new RettsstiftelseId<Rettsstiftelse>(new Long(value));
    }

}
