package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public class StoreUnitOfWork5 extends AbstractStoreSession5 {
    protected final WrappableStoreSession5 wrappedStoreSession;

    public StoreUnitOfWork5(int level, WrappableStoreSession5 wrappedStoreSession, StoreCache5 storeCache) {
        super(level, storeCache);
        this.wrappedStoreSession = wrappedStoreSession;
    }

    public StoreUnitOfWork5 beginUnitOfWork() {
        return new StoreUnitOfWork5(level+1, wrappedStoreSession, storeCache);
    }

    public WrappableStoreSession5 abortUnitOfWork() {
        return this;
    }

    public WrappableStoreSession5 endUnitOfWork() {
        return this;
    }

    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return null;
    }

    WrappableStoreSession5 commitUnitOfWork() {
        return this;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public <T extends BubbleObject> StoreEntry5 registerEntry(int level, T bubbleObject) {
        return wrappedStoreSession.registerEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry5 registerLockedEntry(int level, T bubbleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerTransfer(int level, UnitOfWorkTransfer transfer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        return wrappedStoreSession.evictEntry(level, bubbleId);
    }

    @Override
    public <T extends BubbleObject> StoreEntry5 insertEntry(int level, T bubbleObject) {
        return wrappedStoreSession.insertEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry5 updateEntry(int level, T bubbleObject) {
        return wrappedStoreSession.updateEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry5 deleteEntry(int level, T bubbleObject) {
        return wrappedStoreSession.deleteEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry5> getEntries(int level, Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 loadEntry(int level, I bubbleId) {
        return wrappedStoreSession.loadEntry(level, bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry5> loadEntries(int level, Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 lockEntry(int level, I bubbleId) {
        return wrappedStoreSession.lockEntry(level, bubbleId);
    }
}

