package no.statkart.skif.store;

import com.google.common.collect.Lists;
import no.statkart.skif.exception.ImplementationException;

import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class StoreUnitOfWork extends AbstractStoreSession {
    protected final WrappableStoreSession wrappedStoreSession;

    // Flags to detect improper usage of the unit of work.
    protected boolean accessedAfterLastCallToGetTransfer;
    protected boolean getTransferHasBeenCalled;

    public StoreUnitOfWork(int level, WrappableStoreSession wrappedStoreSession, StoreCache storeCache, Store store) {
        super(level, storeCache);
        this.setStore(store);
        this.wrappedStoreSession = wrappedStoreSession;
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I bubbleId) {
        return wrappedStoreSession.isLocked(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry insertEntry(int level, T bubbleObject) {
        return wrappedStoreSession.insertEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry updateEntry(int level, T bubbleObject) {
        return wrappedStoreSession.updateEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry deleteEntry(int level, T bubbleObject) {
        return wrappedStoreSession.deleteEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean undoEntry(int level, T bubbleObject) {
        return wrappedStoreSession.undoEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        return wrappedStoreSession.loadEntry(level, bubbleId, false);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        return wrappedStoreSession.loadEntries(level, bubbleIds, false);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntriesIgnoreMissing(int level, Set<I> bubbleIds, boolean refresh) {
        return wrappedStoreSession.loadEntriesIgnoreMissing(level, bubbleIds, false);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        StoreEntry entry = wrappedStoreSession.lockEntry(level, bubbleId);
        modifiedMap.put(entry.getId(), entry);
        markModified();
        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry unlockEntry(int level, I bubbleId) {
        return wrappedStoreSession.unlockEntry(level, bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        return wrappedStoreSession.evictEntry(level, bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAllEntries(int level) {
        return wrappedStoreSession.evictAllEntries(level);
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        wrappedStoreSession.ensureFullyLoaded(bubbleObject);
    }

    @Override
    protected boolean isLocked(StoreEntry storeEntry) {
        return storeEntry.isLocked();
    }

    public WrappableStoreSession abortUnitOfWork() {
        Iterator<StoreEntry> iterator = storeCache.values().iterator();
        while (iterator.hasNext()) {
            StoreEntry storeEntry = iterator.next();
            storeEntry.abort(level);
            if (storeEntry.isLockedByLevel(level)) {
                wrappedStoreSession.unlockEntry(level, storeEntry.getId());
            }
            if (storeEntry.getLoadedByLevel() == level) {
                iterator.remove();
            }
        }
        modifiedMap.clear();
        markModified();
        return wrappedStoreSession;
    }

    public WrappableStoreSession endUnitOfWork() {
        if (level != 1) {
            throw new ImplementationException("In nested UnitOfWork. Call commitUnitOfWork() or abortUnitOfWork() instead");
        }
        if (isAccessedAfterGetTransfer()) {
            throw new ImplementationException("Store was accessed between calls to Store.getUnitOfWorkTransfer() and Store.endUnitOfWork() and may result in improper commit");
        }

        if (modifiedMap.size() > 0 && !getTransferHasBeenCalled) {
            // Sjekk at det er kjørt insert, update eller delete på dem, og at de ikke bare er låst.
            boolean allUnmodified = true;
            for (StoreEntry storeEntry : modifiedMap.values()) {
                StoreEntryState state = storeEntry.getState(level);
                if (state != StoreEntryState.UNCHANGED) {
                    allUnmodified = false;
                }
            }
            if (!allUnmodified) {
                throw new ImplementationException("Store contains modified objects. Call getUnitOfWorkTransfer() before calling endUnitOfWork()");
            }
        }
        Iterator<StoreEntry> iterator = storeCache.values().iterator();
        while (iterator.hasNext()) {
            StoreEntry storeEntry = iterator.next();
            if (storeEntry.getLoadedByLevel() == level) {
                iterator.remove();
            } else {
                storeEntry.clear(level);
            }
            storeEntry.lockCreatedByLevel=0;
        }
        modifiedMap.clear();
        markModified();
        return wrappedStoreSession;
    }

    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        accessedAfterLastCallToGetTransfer = false;
        getTransferHasBeenCalled = true;

        return getSnapshot();
    }

    public UnitOfWorkTransfer getSnapshot() {
        List<BubbleObject> insertedObjects = Lists.newArrayList();
        List<BubbleObject> updatedObjects = Lists.newArrayList();
        List<BubbleObject> deletedObjects = Lists.newArrayList();

        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : modifiedMap.entrySet()) {
            StoreEntry storeCacheEntry = mapEntry.getValue();
            StoreEntryState state = storeCacheEntry.getState(level);
            switch (state) {
                case INSERTED:
                    insertedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
                case UPDATED:
                    updatedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
                case DELETED:
                    deletedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
            }
        }
        return new UnitOfWorkTransfer(insertedObjects, updatedObjects, deletedObjects);
    }

    protected boolean isAccessedAfterGetTransfer() {
        return accessedAfterLastCallToGetTransfer && getTransferHasBeenCalled;
    }

    WrappableStoreSession commitUnitOfWork() {

        wrappedStoreSession.commitUnitOfWork(modifiedMap);
        return wrappedStoreSession;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return wrappedStoreSession.getVersions(id, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return wrappedStoreSession.getVersionsForList(ids, start, end);
    }

    @Override
    public void registerEntries(int level, BubbleTransfer<?> bubbleTransfer) {
        wrappedStoreSession.registerEntries(level, bubbleTransfer);
    }
}

