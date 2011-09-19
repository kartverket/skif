package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionUpdateChain extends StoreChain {
    public StoreSessionUpdateChain setNextInWriteChain(StoreSessionUpdateChain next);

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> lock(I bubbleId);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId);

    public <T extends BubbleObject> StoreEntry<T> registerLocked(T bubbleObject);
    public <T extends BubbleObject> StoreEntry<T> registerNew(T bubbleObject);
    public <T extends BubbleObject> StoreEntry<T> registerUpdated(T bubbleObject);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerDeleted(I bubbleId);



}
