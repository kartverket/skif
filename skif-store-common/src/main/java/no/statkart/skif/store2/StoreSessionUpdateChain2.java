package no.statkart.skif.store2;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionUpdateChain2 extends StoreSessionChain2 {
    public StoreSessionUpdateChain2 setNextInWriteChain(StoreSessionUpdateChain2 next);

    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> lock(I bubbleId);
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> lock(Collection<I> bubbleIds);
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean isLocked(I bubbleId);

    public <T extends BubbleObject2> StoreEntry2<T> registerLocked(T bubbleObject);
    public <T extends BubbleObject2> StoreEntry2<T> registerNew(T bubbleObject);
    public <T extends BubbleObject2> StoreEntry2<T> registerUpdated(T bubbleObject);
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> void registerDeleted(I bubbleId);



}
