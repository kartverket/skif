package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class TvangspaategningPaaIdId<T extends TvangspaategningPaaId> extends AbstractStoreTestBubbleId<T> {
    public TvangspaategningPaaIdId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public TvangspaategningPaaIdId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static TvangspaategningPaaIdId<?> create(long value) {
        return new TvangspaategningPaaIdId<TvangspaategningPaaId>(new Long(value));
    }

}
