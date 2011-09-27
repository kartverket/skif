package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestBubbleId<T extends StoreTestBubble> extends BubbleId<T> {

    public StoreTestBubbleId<T> resolveInstance() {
        return this;
    }

    public StoreTestBubbleId() {
        super();
    }

    public StoreTestBubbleId(int idValue) {
        super(new Long(idValue));
    }

    public StoreTestBubbleId(Long idValue) {
        super(idValue);
    }

    public StoreTestBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
