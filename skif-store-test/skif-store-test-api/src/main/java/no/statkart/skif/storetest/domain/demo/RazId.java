package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractNonVersionedBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class RazId<T extends Raz> extends AbstractNonVersionedBubbleId<T> implements StoreTestBubbleId<T> {

    public RazId(Long value) {
        super(value);
    }

    public RazId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
