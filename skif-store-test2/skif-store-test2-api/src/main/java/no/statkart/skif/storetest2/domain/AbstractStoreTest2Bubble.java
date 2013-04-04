package no.statkart.skif.storetest2.domain;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest2.domain.StoreTest2Bubble;

/**
 * @author Roar Ingebrigtsen
 * @since 2.2.0
 */
public abstract class AbstractStoreTest2Bubble extends AbstractBubbleObject implements StoreTest2Bubble {
    private static final long serialVersionUID = 1L;

    @Override
    public AbstractStoreTest2BubbleId<?> getId() {
        return (AbstractStoreTest2BubbleId<?>) super.getId();
    }
}
