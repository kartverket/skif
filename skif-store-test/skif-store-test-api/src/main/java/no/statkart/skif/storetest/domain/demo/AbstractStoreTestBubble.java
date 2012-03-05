package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class AbstractStoreTestBubble extends AbstractBubbleObject implements StoreTestBubble {

    @Override
    public AbstractStoreTestBubbleId<?> getId() {
        return (AbstractStoreTestBubbleId<?>) super.getId();
    }
}
