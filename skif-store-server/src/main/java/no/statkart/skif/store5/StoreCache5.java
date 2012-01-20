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
public class StoreCache5 {
    private Store store;
    private final Map<BubbleId<?>, StoreEntry5> cacheMap;


    public Store getStore() {
        return store;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 get(I bubbleId) {
        return cacheMap.get(bubbleId);
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(int level, I bubbleId) {
        // Todo: FIX
        StoreEntry5 removed = cacheMap.remove(bubbleId);
        return removed!=null;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 remove(I bubbleId) {
        StoreEntry5 storeEntry = cacheMap.remove(bubbleId);
        return storeEntry;
    }

    public StoreCache5() {
        cacheMap = new HashMap<BubbleId<?>, StoreEntry5>(1000);
    }

    public void clear() {
        cacheMap.clear();
    }

    public <T extends BubbleObject> StoreEntry5 registerUnchanged(int level, T bubbleObject) {
        StoreEntry5 entry = new StoreEntry5(bubbleObject.getId());
        entry.setBubbleObject(level, bubbleObject);
        entry.setState(level, StoreEntryState5.UNCHANGED);
        cacheMap.put(bubbleObject.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry5 registerInserted(int level, T bubbleObject) {
        StoreEntry5 entry = new StoreEntry5(level, bubbleObject, StoreEntryState5.INSERTED);
        entry.locked[level]=true;
        cacheMap.put(bubbleObject.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry5 registerNewUpdated(int level, T bubbleObject) {
        StoreEntry5 entry = new StoreEntry5(level, bubbleObject, StoreEntryState5.UPDATED);
        entry.locked[level] = true;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public <T extends BubbleObject> StoreEntry5 registerNewDeleted(int level, T bubbleObject) {
        StoreEntry5 entry = new StoreEntry5(bubbleObject, StoreEntryState5.DELETED);
        entry.locked[level]= true;
        cacheMap.put(entry.getId(), entry);
        return entry;
    }

    public Set<Map.Entry<BubbleId<?>,StoreEntry5>> entrySet () {
        return cacheMap.entrySet();
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
