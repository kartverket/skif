package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author rorchr
 */
public class NivaaIMatrikkelenhetId<T extends NivaaIMatrikkelenhet> extends AbstractStoreTestBubbleId<T> {
    public NivaaIMatrikkelenhetId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public NivaaIMatrikkelenhetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static NivaaIMatrikkelenhetId<?> create(long value) {
        return new NivaaIMatrikkelenhetId<NivaaIMatrikkelenhet>(new Long(value));
    }

}
