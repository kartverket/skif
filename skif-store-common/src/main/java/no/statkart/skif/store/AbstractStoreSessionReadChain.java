package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSessionReadChain extends AbstractStoreChain implements StoreSessionReadChain {
    protected StoreSessionReadChain nextInReadChain;

    @Override
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next) {
        nextInReadChain = next;
        return next;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        return nextInReadChain.get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        return nextInReadChain.get(bubbleIds);
    }



    @Override
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject) {
        return nextInReadChain.register(bubbleObject);
    }

/*
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> lock(I bubbleId) {
        return nextInReadChain.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return nextInReadChain.isLocked(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds) {
        return nextInReadChain.lock(bubbleIds);
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerLocked(T bubbleObject) {
        return nextInReadChain.register(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerNew(T bubbleObject) {
        return nextInReadChain.registerNew(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerUpdated(T bubbleObject) {
        return nextInReadChain.registerUpdated(bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerDeleted(I bubbleId) {
        nextInReadChain.registerDeleted(bubbleId);
    }

*/
}
