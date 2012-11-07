package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SelfBubbleId<T extends SelfBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public SelfBubbleId() {
        super();
    }

    public SelfBubbleId(int idValue) {
        super(new Long(idValue));
    }

    public SelfBubbleId(Long idValue) {
        super(idValue);
    }

    public SelfBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
