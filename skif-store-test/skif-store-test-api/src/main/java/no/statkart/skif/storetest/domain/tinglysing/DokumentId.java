package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @since 2.1
 */
public class DokumentId<T extends Dokument> extends AbstractStoreTestBubbleId<T> {
    public DokumentId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public DokumentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static DokumentId<?> create(long value) {
        return new DokumentId<Dokument>(new Long(value));
    }

}
