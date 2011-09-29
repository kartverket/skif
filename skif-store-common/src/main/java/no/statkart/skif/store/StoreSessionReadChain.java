package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionReadChain extends StoreSessionChain {
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next);


    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds);
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject);
}
