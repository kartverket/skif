package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author rorchr
 */
public class AndelIMatrikkelenhetId<T extends AndelIMatrikkelenhet> extends AbstractStoreTestBubbleId<T> {
    public AndelIMatrikkelenhetId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public AndelIMatrikkelenhetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static AndelIMatrikkelenhetId<?> create(long value) {
        return new AndelIMatrikkelenhetId<AndelIMatrikkelenhet>(new Long(value));
    }

}
