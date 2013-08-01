package no.statkart.skif.storetest.domain.mockup;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class BarId<T extends Bar> extends AbstractStoreTestBubbleId<T> {

    public Long getValue() {
        return (Long) super.getValue();
    }

    public BarId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BarId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
