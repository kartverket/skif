package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestBubble extends BubbleObject {

    public StoreTestBubble() {
    }

    public StoreTestBubble(StoreTestBubbleId<?> id) {
        super(id);
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId((StoreTestBubbleId<?>)id);
    }

    @Override
    public StoreTestBubbleId<?> getId() {
        return (StoreTestBubbleId<?>) super.getId();
    }
}