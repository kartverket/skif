package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class GeometricElementId<T extends GeometricElement> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    public GeometricElementId(Long value) {
        super(value);
    }

    public GeometricElementId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
