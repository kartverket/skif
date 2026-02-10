package no.statkart.skif.store;


import no.statkart.skif.exception.ImplementationException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Henrik Fredholm
 */
public class StoreCache {
    private Store store;
    private final ConcurrentHashMap<BubbleId<?>, StoreEntry> cacheMap;
    private final ConcurrentHashMap<BubbleId<?>, Object> loadLocks;


    public Store getStore() {
        return store;
    }

    public StoreEntry get(BubbleId<?> bubbleId) {
        return cacheMap.get(bubbleId);
    }

    public StoreEntry computeIfAbsent(BubbleId<?> bubbleId, Function<BubbleId<?>, StoreEntry> mappingFunction) {
        StoreEntry existingEntry = cacheMap.get(bubbleId);
        if (existingEntry != null) {
            return existingEntry;
        }

        // This implementation of computeIfAbsent is done "manually" since the mappingFunction
        // might call `loadEntry` or try to register the entry with the cache, and that causes "Recursive Update" exception.
        Object lock = loadLocks.computeIfAbsent(bubbleId, (id) -> new Object());
        synchronized (lock) {
            try {
                existingEntry = cacheMap.get(bubbleId);
                if (existingEntry != null) {
                    return existingEntry;
                }

                StoreEntry computedEntry = mappingFunction.apply(bubbleId);
                if (computedEntry == null) {
                    return null;
                }

                StoreEntry registeredEntry = cacheMap.get(bubbleId);
                return registeredEntry != null ? registeredEntry : computedEntry;
            } finally {
                loadLocks.remove(bubbleId, lock);
            }
        }
    }

    public StoreEntry remove(BubbleId<?> bubbleId) {
        //noinspection UnnecessaryLocalVariable
        StoreEntry storeEntry = cacheMap.remove(bubbleId);
        return storeEntry;
    }

    public StoreCache() {
        cacheMap = new ConcurrentHashMap<>(1000);
        loadLocks = new ConcurrentHashMap<>(1000);
    }

    public void clear() {
        cacheMap.clear();
    }
    public <T extends BubbleObject> StoreEntry register(int loadedByLevel, T bubbleObject) {
        return register(loadedByLevel, bubbleObject, bubbleObject);
    }
    public <T extends BubbleObject> StoreEntry register(int loadedByLevel, T persistentBubbleObject, T bubbleObject) {
        StoreEntry entry = new StoreEntry(bubbleObject.getId().asBase());
        entry.setPersistentBubbleObject(bubbleObject, persistentBubbleObject);
        entry.setState(0, StoreEntryState.UNCHANGED);
        entry.setLoadedByLevel(loadedByLevel);
        StoreEntry oldEntry = cacheMap.put(entry.getId(), entry);
        if (oldEntry!=null) {
            throw new ImplementationException("Duplicate entry:"  + bubbleObject.getId());
        }
        bubbleObject.register(store);
        return entry;
    }

    public Set<Map.Entry<BubbleId<?>,StoreEntry>> entrySet () {
        return cacheMap.entrySet();
    }

    public Collection<StoreEntry> values() {
        return cacheMap.values();
    }

    public Set<BubbleId<?>> keySet() {
        return cacheMap.keySet();
    }
    public void setStore(Store store) {
        this.store = store;
    }

    public StoreEntry createEntry(int level, BubbleId bubbleId) {
        StoreEntry entry = new StoreEntry(bubbleId.asBase());
        entry.setLoadedByLevel(level);
        cacheMap.put(entry.getId(), entry);
        return entry;
    }
}
