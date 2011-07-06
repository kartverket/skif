package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionUpdateChain extends StoreSessionChain {
    public StoreSessionUpdateChain setNextInWriteChain(StoreSessionUpdateChain next);

    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> lock(I bubbleId);
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds);
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean isLocked(I bubbleId);
    
    public <T extends AbstractBubbleObject> StoreEntry<T> registerLocked(T bubbleObject);
    public <T extends AbstractBubbleObject> StoreEntry<T> registerNew(T bubbleObject);
    public <T extends AbstractBubbleObject> StoreEntry<T> registerUpdated(T bubbleObject);
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void registerDeleted(I bubbleId);



}
