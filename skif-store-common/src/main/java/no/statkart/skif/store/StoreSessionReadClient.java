package no.statkart.skif.store;

import com.google.inject.Inject;

import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class StoreSessionReadClient implements StoreSessionReadChain {
    final private StoreReadChain storeService;
    private StoreCache storeCache;

    @Inject
    public StoreSessionReadClient(StoreReadChain storeService) {
        this.storeService = storeService;
    }

    @Override
    public void init(StoreCache storeCache) {
        this.storeCache = storeCache;
    }

    @Override
    public void clear() {
    }

    @Override
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        T bubbleObject = storeService.get(bubbleId);
        StoreEntry<T> newEntry = new StoreEntry<T>(bubbleObject);
        StoreEntry<T> cacheEntry = storeCache.register(newEntry);
        return cacheEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        List<T> bubbleObjects = storeService.get((List<I>) bubbleIds);
        List<StoreEntry<T>> cacheEntries = new ArrayList<StoreEntry<T>>(bubbleObjects.size());
        for (Iterator<T> bubbleObjectIterator = bubbleObjects.iterator(); bubbleObjectIterator.hasNext();) {
            T bubbleObject = bubbleObjectIterator.next();
            StoreEntry<T> newEntry = new StoreEntry<T>(bubbleObject);
            StoreEntry<T> cacheEntry = storeCache.register(newEntry);
            cacheEntries.add(cacheEntry);
        }
        return cacheEntries;
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject) {
        return storeCache.register(new StoreEntry<T>(bubbleObject));
    }
}
