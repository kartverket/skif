package no.statkart.skif.store2;

import no.statkart.skif.store2.*;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSessionReadChain2 extends AbstractStoreSessionChain2 implements StoreSessionReadChain2 {
    protected StoreSessionReadChain2 nextInReadChain;

    @Override
    public StoreSessionReadChain2 setNextInReadChain(StoreSessionReadChain2 next) {
        nextInReadChain = next;
        return next;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId) {
        return nextInReadChain.get(bubbleId);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> get(Collection<I> bubbleIds) {
        return nextInReadChain.get(bubbleIds);
    }



    @Override
    public <T extends BubbleObject2> StoreEntry2<T> register(T bubbleObject) {
        return nextInReadChain.register(bubbleObject);
    }

/*
    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> lock(I bubbleId) {
        return nextInReadChain.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean isLocked(I bubbleId) {
        return nextInReadChain.isLocked(bubbleId);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> lock(Collection<I> bubbleIds) {
        return nextInReadChain.lock(bubbleIds);
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerLocked(T bubbleObject) {
        return nextInReadChain.register(bubbleObject);
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerNew(T bubbleObject) {
        return nextInReadChain.registerNew(bubbleObject);
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerUpdated(T bubbleObject) {
        return nextInReadChain.registerUpdated(bubbleObject);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> void registerDeleted(I bubbleId) {
        nextInReadChain.registerDeleted(bubbleId);
    }

*/
}
