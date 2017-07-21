package no.statkart.skif.storetest.domain.multikobling;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * Id for {@link Multirefererende}.
 *
 * @author Tor Egil R. String
 * @since 2.2.0
 */
public class MultirefererendeId<T extends Multirefererende> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public MultirefererendeId(Long value) {
        super(value);
    }

    public MultirefererendeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
