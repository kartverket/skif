package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class FestekontraktId<T extends Festekontrakt> extends AbstractStoreTestBubbleId<T> {
    public FestekontraktId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public FestekontraktId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static FestekontraktId<?> create(long value) {
        return new FestekontraktId<Festekontrakt>(new Long(value));
    }
}