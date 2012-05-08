package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author rorchr
 */
public class RettsstiftelseRelasjonId<T extends RettsstiftelseRelasjon> extends AbstractStoreTestBubbleId<T> {
    public RettsstiftelseRelasjonId(Long value) {
        super(value);
    }

    public RettsstiftelseRelasjonId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static RettsstiftelseRelasjonId<?> create(long value) {
        return new RettsstiftelseRelasjonId<RettsstiftelseRelasjon>(new Long(value));
    }

}
