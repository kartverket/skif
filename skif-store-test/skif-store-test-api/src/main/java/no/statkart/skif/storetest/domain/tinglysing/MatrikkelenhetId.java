package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @since 2.1
 */
public class MatrikkelenhetId<T extends Matrikkelenhet> extends AbstractStoreTestBubbleId<T> {
    public MatrikkelenhetId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public MatrikkelenhetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static MatrikkelenhetId<?> create(long value) {
        return new MatrikkelenhetId<Matrikkelenhet>(new Long(value));
    }

}
