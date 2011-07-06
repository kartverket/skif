package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractWriteStoreChain extends AbstractStoreSessionChain implements StoreSessionUpdateChain {
    protected StoreSessionUpdateChain nextInWriteChain;

    @Override
    public StoreSessionUpdateChain setNextInWriteChain(StoreSessionUpdateChain next) {
        nextInWriteChain = next;
        return this;
    }

   @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return nextInWriteChain.isLocked(bubbleId);
    }
    
}