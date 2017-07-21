package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class BarFoosId<T extends BarFoos> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    public Long getValue() {
        return (Long) super.getValue();
    }

    @SuppressWarnings("unused")
    public BarFoosId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BarFoosId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
