package no.statkart.skif.storetest.domain;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.AbstractNonVersionedBubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class BazId<T extends Baz> extends AbstractNonVersionedBubbleId<T> implements StoreTestBubbleId<T> {

    public BazId(Long value) {
        super(value);
    }

    public BazId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
