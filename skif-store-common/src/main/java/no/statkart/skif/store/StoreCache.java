package no.statkart.skif.store;


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
        cacheMap.put(bubbleObject.getId(), entry);

        return entry;
    }

    public <T extends BubbleObject> StoreEntry registerUnchanged(int level, T bubbleObject) {
        StoreEntry entry = new StoreEntry(bubbleObject.getId());
        entry.setBubbleObject(level, bubbleObject);
        entry.setState(level, StoreEntryState.UNCHANGED);
        cacheMap.put(bubbleObject.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry registerInserted(int level, T bubbleObject) {
        StoreEntry entry = new StoreEntry(level, bubbleObject, StoreEntryState.INSERTED);
        entry.locked[level]=true;
        cacheMap.put(bubbleObject.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry registerNewUpdated(int level, T bubbleObject) {
        StoreEntry entry = new StoreEntry(level, bubbleObject, StoreEntryState.UPDATED);
        entry.locked[level] = true;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry registerNewDeleted(int level, T bubbleObject) {
        StoreEntry entry = new StoreEntry(bubbleObject, StoreEntryState.DELETED);
        entry.locked[level]= true;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public Set<Map.Entry<BubbleId<?>,StoreEntry>> entrySet () {
        return cacheMap.entrySet();
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public <T extends BubbleObject> StoreEntry registerLocked(int level, T processedBubbleObject) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public StoreEntry createEntry(int level, BubbleId bubbleId) {
        StoreEntry entry = new StoreEntry(bubbleId);
        entry.setLoadedByLevel(level);
        cacheMap.put(entry.getId(), entry);
        return entry;
    }
}
