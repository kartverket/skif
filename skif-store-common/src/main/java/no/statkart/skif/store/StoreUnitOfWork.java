package no.statkart.skif.store;

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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        return wrappedStoreSession.loadEntry(level, bubbleId, false);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        return wrappedStoreSession.loadEntries(level, bubbleIds, false);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        StoreEntry entry = wrappedStoreSession.lockEntry(level, bubbleId);
        modifiedMap.put(entry.getId(), entry);
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
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        wrappedStoreSession.ensureFullyLoaded(bubbleObject);
    }

    public WrappableStoreSession abortUnitOfWork() {
        for (StoreEntry storeEntry : modifiedMap.values()) {
            storeEntry.abort(level);
            if (storeEntry.isLockedByLevel(level)) {
                wrappedStoreSession.unlockEntry(level, storeEntry.getId());
            }
            if (storeEntry.getLoadedByLevel()==level) {
                storeCache.remove(storeEntry.getId());
            }
        }
        modifiedMap.clear();
        return wrappedStoreSession;
    }

    public WrappableStoreSession endUnitOfWork() {
        // TODO: check modified etter getUnitOfWorkTransfer
        return wrappedStoreSession;
    }

    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        accessedAfterLastCallToGetTransfer = false;
        getTransferHasBeenCalled = true;

        return getSnapshot();
    }

    public UnitOfWorkTransfer getSnapshot() {
        LinkedHashSet<BubbleId<?>> newIds = new LinkedHashSet<BubbleId<?>>();
        LinkedHashSet<BubbleId<?>> updatedIds = new LinkedHashSet<BubbleId<?>>();
        LinkedHashSet<BubbleId<?>> deletedIds = new LinkedHashSet<BubbleId<?>>();

        Map newAndUpdatedObjects = new HashMap();
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : modifiedMap.entrySet()) {
            StoreEntry storeCacheEntry = mapEntry.getValue();
            StoreEntryState state = storeCacheEntry.getState(level);
            switch (state) {
                case INSERTED:
                    newIds.add(mapEntry.getKey());
                    newAndUpdatedObjects.put(mapEntry.getKey(), storeCacheEntry.getBubbleObject(level));
                    break;
                case UPDATED:
                    updatedIds.add(mapEntry.getKey());
                    newAndUpdatedObjects.put(mapEntry.getKey(), storeCacheEntry.getBubbleObject(level));
                    break;
                case DELETED:
                    deletedIds.add(mapEntry.getKey());
                    break;
            }
        }
        return new UnitOfWorkTransfer(newAndUpdatedObjects, deletedIds, newIds, updatedIds);
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return wrappedStoreSession.getVersionsForList(ids, start, end);
    }


}

