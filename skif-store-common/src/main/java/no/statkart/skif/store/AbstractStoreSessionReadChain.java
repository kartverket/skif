package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSessionReadChain extends AbstractStoreSessionChain implements StoreSessionReadChain {
    protected StoreSessionReadChain nextInReadChain;

    @Override
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next) {
        nextInReadChain = next;
        return next;
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        return nextInReadChain.get(bubbleId);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        return nextInReadChain.get(bubbleIds);
    }



    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> register(T bubbleObject) {
        return nextInReadChain.register(bubbleObject);
    }

/*
    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> lock(I bubbleId) {
        return nextInReadChain.lock(bubbleId);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return nextInReadChain.isLocked(bubbleId);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds) {
        return nextInReadChain.lock(bubbleIds);
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerLocked(T bubbleObject) {
        return nextInReadChain.register(bubbleObject);
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerNew(T bubbleObject) {
        return nextInReadChain.registerNew(bubbleObject);
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerUpdated(T bubbleObject) {
        return nextInReadChain.registerUpdated(bubbleObject);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void registerDeleted(I bubbleId) {
        nextInReadChain.registerDeleted(bubbleId);
    }

*/
}
