package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class BarId<T extends Bar> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    public BarId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public BarId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
