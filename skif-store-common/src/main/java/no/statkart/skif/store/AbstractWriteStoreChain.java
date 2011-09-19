package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractWriteStoreChain extends AbstractStoreChain implements StoreUpdateChain {
    protected StoreUpdateChain nextInWriteChain;

    @Override
    public StoreUpdateChain setNextInWriteChain(StoreUpdateChain next) {
        nextInWriteChain = next;
        return this;
    }

   @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return nextInWriteChain.isLocked(bubbleId);
    }
    
}