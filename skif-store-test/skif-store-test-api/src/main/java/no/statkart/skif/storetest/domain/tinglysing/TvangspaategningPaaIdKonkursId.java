package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Oddbjørn Kvalsund
 */
public class TvangspaategningPaaIdKonkursId<T extends TvangspaategningPaaIdKonkurs> extends AbstractStoreTestBubbleId<T> {
    public TvangspaategningPaaIdKonkursId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public TvangspaategningPaaIdKonkursId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static TvangspaategningPaaIdKonkursId<?> create(long value) {
        return new TvangspaategningPaaIdKonkursId<TvangspaategningPaaIdKonkurs>(new Long(value));
    }

}
