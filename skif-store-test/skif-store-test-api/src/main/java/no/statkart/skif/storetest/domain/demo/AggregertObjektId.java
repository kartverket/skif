package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * Id for testing av aggergering av objekter.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class AggregertObjektId<T extends AggregertObjekt> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {
    public Long getValue() {
        return (Long) super.getValue();
    }

    public AggregertObjektId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public AggregertObjektId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
