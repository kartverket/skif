package no.statkart.skif.store5;


import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
}
