package no.statkart.skif.store5;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public class StoreUnitOfWork extends AbstractStoreSession {
    protected final WrappableStoreSession wrappedStoreSession;

    public StoreUnitOfWork(int level, WrappableStoreSession wrappedStoreSession, StoreCache storeCache) {
        super(level, storeCache);
        this.wrappedStoreSession = wrappedStoreSession;
    }

    public StoreUnitOfWork beginUnitOfWork() {
        return new StoreUnitOfWork(level+1, wrappedStoreSession, storeCache);
    }

    public WrappableStoreSession abortUnitOfWork() {
        return this;
    }

    public WrappableStoreSession endUnitOfWork() {
        return this;
    }

    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return null;
    }

    WrappableStoreSession commitUnitOfWork() {
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
    public <T extends BubbleObject> StoreEntry registerEntry(int level, T bubbleObject) {
        return wrappedStoreSession.registerEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry registerLockedEntry(int level, T bubbleObject) {
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
    public <T extends BubbleObject> StoreEntry insertEntry(int level, T bubbleObject) {
        return wrappedStoreSession.insertEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry updateEntry(int level, T bubbleObject) {
        return wrappedStoreSession.updateEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject> StoreEntry deleteEntry(int level, T bubbleObject) {
        return wrappedStoreSession.deleteEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> getEntries(int level, Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId) {
        return wrappedStoreSession.loadEntry(level, bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        return wrappedStoreSession.lockEntry(level, bubbleId);
    }
}

