package no.statkart.skif.storetest.domain.multikobling.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public class BubbleWithEntityInMultikoblingId<T extends BubbleWithEntityInMultikobling> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public BubbleWithEntityInMultikoblingId(Long value) {
        super(value);
    }

    public BubbleWithEntityInMultikoblingId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
