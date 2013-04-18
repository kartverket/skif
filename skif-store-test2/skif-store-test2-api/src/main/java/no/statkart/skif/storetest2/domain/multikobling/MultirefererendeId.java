package no.statkart.skif.storetest2.domain.multikobling;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.AbstractStoreTest2BubbleId;

/**
 * Id for {@link Multirefererende}.
 *
 * @author Tor Egil R. String
 * @since 2.2.0
 */
public class MultirefererendeId<T extends Multirefererende> extends AbstractStoreTest2BubbleId<T> {
    public MultirefererendeId(Long value) {
        super(value);
    }

    public MultirefererendeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
