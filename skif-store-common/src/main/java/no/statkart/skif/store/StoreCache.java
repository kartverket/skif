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

    public StoreEntry get(BubbleId<?> bubbleId) {
        return cacheMap.get(bubbleId);
    }

    public StoreEntry remove(BubbleId<?> bubbleId) {
        //noinspection UnnecessaryLocalVariable
        StoreEntry storeEntry = cacheMap.remove(bubbleId);
        return storeEntry;
    }

    public StoreCache() {
        cacheMap = new HashMap<>(1000);
    }

    public void clear() {
        cacheMap.clear();
    }
    public <T extends BubbleObject> StoreEntry register(int loadedByLevel, T bubbleObject) {
        return register(loadedByLevel, bubbleObject, bubbleObject);
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
        StoreEntry entry = new StoreEntry(bubbleId);
        entry.setLoadedByLevel(level);
        cacheMap.put(entry.getId(), entry);
        return entry;
    }
}
