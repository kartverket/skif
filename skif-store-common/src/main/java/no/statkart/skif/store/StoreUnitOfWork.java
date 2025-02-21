package no.statkart.skif.store;

import com.google.common.collect.Sets;
import no.statkart.skif.exception.ImplementationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public class StoreUnitOfWork extends AbstractStoreSession {
    protected final WrappableStoreSession wrappedStoreSession;

    // Flags to detect improper usage of the unit of work.
    protected boolean accessedAfterLastCallToGetTransfer;
    protected boolean getTransferHasBeenCalled;

    public StoreUnitOfWork(int level, WrappableStoreSession wrappedStoreSession, StoreCache storeCache, AbstractStore store) {
        super(level, storeCache);
        this.setStore(store);
        this.wrappedStoreSession = wrappedStoreSession;
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I bubbleId) {
        return wrappedStoreSession.isLocked(bubbleId);
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
    public <T extends BubbleObject> boolean undoEntry(int level, T bubbleObject) {
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
        addLocked(entry);
        markModified();
        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> lockEntries(int level, Set<I> bubbleIds) {
        Collection<StoreEntry> entries = wrappedStoreSession.lockEntries(level, bubbleIds);
        for (StoreEntry entry : entries) {
            addLocked(entry);
        }
        markModified();
        return entries;
    }

    @Override
    public StoreEntry unlockEntry(int level, BubbleId<?> bubbleId) {
        return wrappedStoreSession.unlockEntry(level, bubbleId);
    }

    @Override
    public Collection<StoreEntry> unlockEntries(int level, Collection<? extends BubbleId<?>> bubbleIds) {
        return wrappedStoreSession.unlockEntries(level, bubbleIds);
    }

    @Override
    public boolean evictEntry(int level, BubbleId<?> bubbleId) {
        return wrappedStoreSession.evictEntry(level, bubbleId);
    }

    @Override
    public boolean evictAllEntries(int level) {
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
        List<StoreEntry> removeEntries = new ArrayList<>();
        List<StoreEntry> clearEntries = new ArrayList<>();
        Set<BubbleId<?>> unlockIds = new HashSet<>();

        for (StoreEntry storeEntry : storeCache.values()) {
            if (storeEntry.isLockedByLevel(level)) {
                unlockIds.add(storeEntry.getId());
            }
            boolean removeEntry = storeEntry.abort(level);
            if (removeEntry) {
                removeEntries.add(storeEntry);
            } else {
                clearEntries.add(storeEntry);
            }
        }

        if (!unlockIds.isEmpty()) {
            wrappedStoreSession.unlockEntries(level, unlockIds);
        }

        removeEntries.stream().map(StoreEntry::getId).forEach(storeCache::remove);
        clearEntries.forEach(storeEntry -> storeEntry.clear(level));

        modifiedMap.clear();
        markModified();
        return wrappedStoreSession;
    }

    public WrappableStoreSession endUnitOfWork() {
        if (isAccessedAfterGetTransfer()) {
            throw new ImplementationException("Store was accessed between calls to Store.getUnitOfWorkTransfer() and Store.endUnitOfWork() and may result in improper commit");
        }

        if (modifiedMap.size() > 0 && !getTransferHasBeenCalled) {
            throw new ImplementationException("Store contains modified objects. Call getUnitOfWorkTransfer() before calling endUnitOfWork()");
        }
        Iterator<StoreEntry> iterator = storeCache.values().iterator();
        while (iterator.hasNext()) {
            StoreEntry storeEntry = iterator.next();
            if (storeEntry.getLoadedByLevel() == level) {
                iterator.remove();
            } else {
                storeEntry.clear(level);
            }
            storeEntry.lockCreatedByLevel = 0;
        }
        modifiedMap.clear();
        markModified();
        return wrappedStoreSession;
    }

    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        UnitOfWorkTransfer snapshot = getUnitOfWorkSnapshot();

        StoreUnitOfWork uow = this;
        while (uow != null) {
            uow.accessedAfterLastCallToGetTransfer = false;
            uow.getTransferHasBeenCalled = true;

            uow = uow.wrappedStoreSession instanceof StoreUnitOfWork ? (StoreUnitOfWork) uow.wrappedStoreSession : null;
        }

        return snapshot;
    }

    private UnitOfWorkTransfer getUnitOfWorkSnapshot() {
        if (wrappedStoreSession instanceof StoreUnitOfWork) {
            return addSessionSnapshot(((StoreUnitOfWork) wrappedStoreSession).getUnitOfWorkSnapshot());
        } else {
            return getSessionSnapshot(); // Underliggende er ikke en unit-of-work og skal ikke tas med her
        }
    }

    @Override
    public UnitOfWorkTransfer getSnapshot() {
        if (wrappedStoreSession instanceof StoreSessionClient) {
            return getSessionSnapshot(); //  Underliggende er av type StoreSessionClient og kan ikke ha endringer
        } else {
            return addSessionSnapshot(wrappedStoreSession.getSnapshot());
        }
    }

    public UnitOfWorkTransfer addSessionSnapshot(UnitOfWorkTransfer snapshot) {
        Set<BubbleId<?>> modifiedIds = Sets.newLinkedHashSet(modifiedMap.keySet());
        List<BubbleObject> insertedObjects = new ArrayList<>(snapshot.getInsertedObjects());
        List<BubbleObject> updatedObjects = new ArrayList<>(snapshot.getUpdatedObjects());
        List<BubbleObject> deletedObjects = new ArrayList<>(snapshot.getDeletedObjects());

        for (ListIterator<BubbleObject> iterator = insertedObjects.listIterator(); iterator.hasNext(); ) {
            BubbleObject insertedObject = iterator.next();
            StoreEntry storeCacheEntry = modifiedMap.get(insertedObject.getId());
            if (storeCacheEntry != null) {
                StoreEntryState state = storeCacheEntry.getState(level);
                switch (state) {
                    case INSERTED:
                        // Dette skal egentlig ikke være mulig. Anser insert etter insert som update etter insert.
                    case DELETED_INSERTED:
                    case UPDATED:
                        iterator.set(storeCacheEntry.getBubbleObject(level));
                        break;
                    case DELETED:
                        iterator.remove();
                        break;
                }
                modifiedIds.remove(insertedObject.getId());
            }
        }
        for (ListIterator<BubbleObject> iterator = updatedObjects.listIterator(); iterator.hasNext(); ) {
            BubbleObject updatedObject = iterator.next();
            StoreEntry storeCacheEntry = modifiedMap.get(updatedObject.getId());
            if (storeCacheEntry != null) {
                StoreEntryState state = storeCacheEntry.getState(level);
                switch (state) {
                    case INSERTED:
                        // Dette skal egentlig ikke være mulig. Anser insert etter update som update etter update.
                    case DELETED_INSERTED:
                    case UPDATED:
                        iterator.set(storeCacheEntry.getBubbleObject(level));
                        break;
                    case DELETED:
                        iterator.remove();
                        deletedObjects.add(storeCacheEntry.getBubbleObject(level));
                        break;
                }
                modifiedIds.remove(updatedObject.getId());
            }
        }
        for (ListIterator<BubbleObject> iterator = deletedObjects.listIterator(); iterator.hasNext(); ) {
            BubbleObject deletedObject = iterator.next();
            StoreEntry storeCacheEntry = modifiedMap.get(deletedObject.getId());
            if (storeCacheEntry != null) {
                StoreEntryState state = storeCacheEntry.getState(level);
                switch (state) {
                    case INSERTED:
                        iterator.remove();
                        updatedObjects.add(storeCacheEntry.getBubbleObject(level)); // Det som blir inserted kan være endret fra det som ble deleted
                    case DELETED_INSERTED:
                    case UPDATED:
                        throw new ImplementationException("Can not update deleted object");
                    case DELETED:
                        // OK
                        break;
                }
                modifiedIds.remove(deletedObject.getId());
            }
        }

        for (BubbleId<?> modifiedId : modifiedIds) {
            StoreEntry storeCacheEntry = modifiedMap.get(modifiedId);
            StoreEntryState state = storeCacheEntry.getState(level);
            switch (state) {
                case INSERTED:
                    insertedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
                case DELETED_INSERTED:
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
        LinkedHashMap<BubbleId<?>, StoreEntry> lockedOrModified = new LinkedHashMap<>(modifiedMap);
        lockedOrModified.putAll(lockedMap);
        wrappedStoreSession.commitUnitOfWork(lockedOrModified);
        return wrappedStoreSession;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return wrappedStoreSession.getVersions(id, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        return wrappedStoreSession.getVersionsForList(ids, start, end);
    }

    @Override
    public Collection<StoreEntry> registerEntries(int level, Transfer<?> transfer) {
        return wrappedStoreSession.registerEntries(level, transfer);
    }

    @Override
    public BubbleObject getPersistedBubbleObjectForLocked(StoreEntry storeEntry) {
        return wrappedStoreSession.getPersistedBubbleObjectForLocked(storeEntry);
    }
}

