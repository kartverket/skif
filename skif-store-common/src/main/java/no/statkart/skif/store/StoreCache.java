package no.statkart.skif.store;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static no.statkart.skif.guava.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 */
public class StoreCache {
    private Store store;
    Map<BubbleId<?>, StoreEntry<?>> cacheMap;


    public Store getStore() {
        return store;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        return (StoreEntry<T>) cacheMap.get(bubbleId);
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> remove(I bubbleId) {
        StoreEntry<?> storeEntry = cacheMap.remove(bubbleId);
        storeEntry.setBubbleObject(null);
        return (StoreEntry<T>) storeEntry;
    }

    public void init(Store store) {
        this.store = store;
        cacheMap = new HashMap<BubbleId<?>, StoreEntry<?>>(1000);

    }

    public void clear() {
        cacheMap.clear();
    }

    public <T extends BubbleObject> StoreEntry<T> register(StoreEntry<T> newEntry) {
        StoreEntry<T> entry = (StoreEntry<T>) cacheMap.get(newEntry.getId());
        if (entry == null) {
            entry = newEntry;
            entry.getBubbleObject().register(store);
            cacheMap.put(entry.getId(), entry);
        } else {
            entry.replaceIfExistingIsUnlockedAndOlder(newEntry, store, StoreEntryState.UNLOCKED);
        }
        return entry;
    }

    public <T extends BubbleObject> StoreEntry<T> registerLocked(StoreEntry<T> newEntry) {
        StoreEntry<T> entry = (StoreEntry<T>) cacheMap.get(newEntry.getId());
        if (entry == null) {
            entry = newEntry;
            entry.getBubbleObject().register(store);
            entry.state = StoreEntryState.LOCKED;
            cacheMap.put(entry.getId(), entry);
        } else {
            entry.replaceIfExistingIsUnlockedAndOlder(newEntry, store, StoreEntryState.LOCKED);
        }
        return entry;
    }

    public <T extends BubbleObject> StoreEntry<T> registerNew(StoreEntry<T> newEntry) {
        StoreEntry<T> entry = (StoreEntry<T>) cacheMap.get(newEntry.getId());
        checkState(entry == null, "BubbleObject has already been registered. Cannot register object as new", newEntry.getId());
        entry = newEntry;
        entry.getBubbleObject().register(store);
        entry.state = StoreEntryState.NEW;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry<T> registerUpdated(StoreEntry<T> newEntry) {
        StoreEntry<T> entry = (StoreEntry<T>) cacheMap.get(newEntry.getId());
        checkState(entry == null, "BubbleObject has already been registered. Cannot register object as updated", newEntry.getId());
        entry = newEntry;
        entry.getBubbleObject().register(store);
        entry.state = StoreEntryState.UPDATED;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry<T> registerDeleted(StoreEntry<T> newEntry) {
        StoreEntry<T> entry = (StoreEntry<T>) cacheMap.get(newEntry.getId());
        checkState(entry == null, "BubbleObject has already been registered. Cannot register object as deleted", newEntry.getId());
        entry = newEntry;
        entry.state = StoreEntryState.DELETED;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public Set<Map.Entry<BubbleId<?>,StoreEntry<?>>> entrySet () {
        return cacheMap.entrySet();
    }
}
