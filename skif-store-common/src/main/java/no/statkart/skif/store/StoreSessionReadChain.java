package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionReadChain extends StoreSessionChain {
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next);


    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> get(I bubbleId);
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds);
    public <T extends AbstractBubbleObject> StoreEntry<T> register(T bubbleObject);
}
