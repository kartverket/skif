package no.statkart.skif.store2;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static no.statkart.skif.guava.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 */
public class StoreCache2 {
    private Store2 store;
    Map<BubbleId2<?>, StoreEntry2<?>> cacheMap;


    public Store2 getStore() {
        return store;
    }

    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId) {
        return (StoreEntry2<T>) cacheMap.get(bubbleId);
    }

    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> remove(I bubbleId) {
        StoreEntry2<?> storeEntry = cacheMap.remove(bubbleId);
        storeEntry.setBubbleObject(null);
        return (StoreEntry2<T>) storeEntry;
    }

    public void init(Store2 store) {
        this.store = store;
        cacheMap = new HashMap<BubbleId2<?>, StoreEntry2<?>>(1000);

    }

    public void clear() {
        cacheMap.clear();
    }

    public <T extends BubbleObject2> StoreEntry2<T> register(StoreEntry2<T> newEntry) {
        StoreEntry2<T> entry = (StoreEntry2<T>) cacheMap.get(newEntry.getId());
        if (entry == null) {
            entry = newEntry;
            entry.getBubbleObject().register(store);
            cacheMap.put(entry.getId(), entry);
        } else {
            entry.replaceIfExistingIsUnlockedAndOlder(newEntry, store, StoreEntryState2.UNLOCKED);
        }
        return entry;
    }

    public <T extends BubbleObject2> StoreEntry2<T> registerLocked(StoreEntry2<T> newEntry) {
        StoreEntry2<T> entry = (StoreEntry2<T>) cacheMap.get(newEntry.getId());
        if (entry == null) {
            entry = newEntry;
            entry.getBubbleObject().register(store);
            entry.state = StoreEntryState2.LOCKED;
            cacheMap.put(entry.getId(), entry);
        } else {
            entry.replaceIfExistingIsUnlockedAndOlder(newEntry, store, StoreEntryState2.LOCKED);
        }
        return entry;
    }

    public <T extends BubbleObject2> StoreEntry2<T> registerNew(StoreEntry2<T> newEntry) {
        StoreEntry2<T> entry = (StoreEntry2<T>) cacheMap.get(newEntry.getId());
        checkState(entry == null, "BubbleObject2 has already been registered. Cannot register object as new", newEntry.getId());
        entry = newEntry;
        entry.getBubbleObject().register(store);
        entry.state = StoreEntryState2.NEW;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject2> StoreEntry2<T> registerUpdated(StoreEntry2<T> newEntry) {
        StoreEntry2<T> entry = (StoreEntry2<T>) cacheMap.get(newEntry.getId());
        checkState(entry == null, "BubbleObject2 has already been registered. Cannot register object as updated", newEntry.getId());
        entry = newEntry;
        entry.getBubbleObject().register(store);
        entry.state = StoreEntryState2.UPDATED;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject2> StoreEntry2<T> registerDeleted(StoreEntry2<T> newEntry) {
        StoreEntry2<T> entry = (StoreEntry2<T>) cacheMap.get(newEntry.getId());
        checkState(entry == null, "BubbleObject2 has already been registered. Cannot register object as deleted", newEntry.getId());
        entry = newEntry;
        entry.state = StoreEntryState2.DELETED;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public Set<Map.Entry<BubbleId2<?>,StoreEntry2<?>>> entrySet () {
        return cacheMap.entrySet();
    }
}
