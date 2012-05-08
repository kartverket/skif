package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class EndringAvJordsameieId<T extends EndringAvJordsameie> extends AbstractStoreTestBubbleId<T> {
    public EndringAvJordsameieId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public EndringAvJordsameieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static HjemmelId<?> create(long value) {
        return new HjemmelId<EndringAvJordsameie>(new Long(value));
    }

}
