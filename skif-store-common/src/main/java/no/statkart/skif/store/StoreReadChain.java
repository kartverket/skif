package no.statkart.skif.store;

import java.util.List;

/**
 * @author Henrik Fredholm
 */
public interface StoreReadChain {
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> T get(I bubbleId);
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> List<T> get(List<I> bubbleIds);
}
