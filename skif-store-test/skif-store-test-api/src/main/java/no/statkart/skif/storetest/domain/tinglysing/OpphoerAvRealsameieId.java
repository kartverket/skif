package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class OpphoerAvRealsameieId<T extends OpphoerAvRealsameie> extends AbstractStoreTestBubbleId<T> {
    public OpphoerAvRealsameieId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public OpphoerAvRealsameieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static OpphoerAvRealsameieId<?> create(long value) {
        return new OpphoerAvRealsameieId<OpphoerAvRealsameie>(new Long(value));
    }
}