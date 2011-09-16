package no.statkart.skif.store;

import java.util.List;

/**
 * @author Henrik Fredholm
 */
public interface StoreReadChain {
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds);
}
