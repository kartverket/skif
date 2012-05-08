package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class OpphoerAvJordsameieId<T extends OpphoerAvJordsameie> extends AbstractStoreTestBubbleId<T> {
    public OpphoerAvJordsameieId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public OpphoerAvJordsameieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static OpphoerAvJordsameieId<?> create(long value) {
        return new OpphoerAvJordsameieId<OpphoerAvJordsameie>(new Long(value));
    }

}