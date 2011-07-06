package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public class UnitOfWorkImpl extends AbstractStoreSessionReadChain implements StoreSessionUpdateChain, UnitOfWorkChain{
    final protected StoreSessionUpdateChain nextInWriteChain;

    public UnitOfWorkImpl(StoreSessionUpdateChain nextInWriteChain) {
        this.nextInWriteChain = nextInWriteChain;
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> get(I id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> lock(I id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerLocked(T bubbleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerNew(T bubbleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerUpdated(T bubbleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void registerDeleted(I bubbleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StoreSessionUpdateChain setNextInWriteChain(StoreSessionUpdateChain next) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void startUnitOfWork() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return null;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abortUnitOfWork() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void endUnitOfWork() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean inUnitOfWork() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
