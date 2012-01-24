package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ChildBubbleId<T extends ChildBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ChildBubbleId() {
        super();
    }

    public ChildBubbleId(int idValue) {
        super(new Long(idValue));
    }

    public ChildBubbleId(Long idValue) {
        super(idValue);
    }

    public ChildBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
