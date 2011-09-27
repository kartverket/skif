package no.statkart.skif.store2;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionReadChain2 extends StoreSessionChain2 {
    public StoreSessionReadChain2 setNextInReadChain(StoreSessionReadChain2 next);


    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId);
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> get(Collection<I> bubbleIds);
    public <T extends BubbleObject2> StoreEntry2<T> register(T bubbleObject);
}
