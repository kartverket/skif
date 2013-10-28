package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class AbstractStoreTestBubble extends AbstractBubbleObject implements StoreTestBubble {
    public AbstractStoreTestBubble(BubbleId<?> id) {
        super(id);
    }

    public AbstractStoreTestBubble() {
    }

    @Override
    public AbstractStoreTestBubbleId<?> getId() {
        return (AbstractStoreTestBubbleId<?>) super.getId();
    }
}
