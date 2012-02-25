package no.statkart.skif.store;


import no.statkart.skif.exception.ImplementationException;

import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class StoreCache {
    private Store store;
    private final Map<BubbleId<?>, StoreEntry> cacheMap;


    public Store getStore() {
        return store;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry get(I bubbleId) {
        return cacheMap.get(bubbleId);
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(int level, I bubbleId) {
        // Todo: FIX
        StoreEntry removed = cacheMap.remove(bubbleId);
        return removed!=null;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry remove(I bubbleId) {
        StoreEntry storeEntry = cacheMap.remove(bubbleId);
        return storeEntry;
    }

    public StoreCache() {
        cacheMap = new HashMap<BubbleId<?>, StoreEntry>(1000);
    }

    public void clear() {
        cacheMap.clear();
    }

    public <T extends BubbleObject> StoreEntry register(int loadedByLevel, T persistentBubbleObject, T bubbleObject) {
        StoreEntry entry = new StoreEntry(bubbleObject.getId());
        entry.setPersistentBubbleObject(bubbleObject, persistentBubbleObject);
        entry.setState(0, StoreEntryState.UNCHANGED);
        entry.setLoadedByLevel(loadedByLevel);
        StoreEntry oldEntry = cacheMap.put(bubbleObject.getId(), entry);
        if (oldEntry!=null) {
            throw new ImplementationException("Duplicate entry:"  + bubbleObject.getId());
        }
        bubbleObject.register(store);
        return entry;
    }

    public Set<Map.Entry<BubbleId<?>,StoreEntry>> entrySet () {
        return cacheMap.entrySet();
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public StoreEntry createEntry(int level, BubbleId bubbleId) {
        StoreEntry entry = new StoreEntry(bubbleId);
        entry.setLoadedByLevel(level);
        cacheMap.put(entry.getId(), entry);
        return entry;
    }
}
