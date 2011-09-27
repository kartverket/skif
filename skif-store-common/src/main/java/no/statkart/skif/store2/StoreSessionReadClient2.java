package no.statkart.skif.store2;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class StoreSessionReadClient2 implements StoreSessionReadChain2 {
    final private StoreReadChain2 storeService;
    private StoreCache2 storeCache;

    @Inject
    public StoreSessionReadClient2(StoreReadChain2 storeService) {
        this.storeService = storeService;
    }

    @Override
    public void init(StoreCache2 storeCache) {
        this.storeCache = storeCache;
    }

    @Override
    public void clear() {
    }

    @Override
    public StoreSessionReadChain2 setNextInReadChain(StoreSessionReadChain2 next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId) {
        T bubbleObject = storeService.get(bubbleId);
        StoreEntry2<T> newEntry = new StoreEntry2<T>(bubbleObject);
        StoreEntry2<T> cacheEntry = storeCache.register(newEntry);
        return cacheEntry;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> get(Collection<I> bubbleIds) {
        List<T> bubbleObjects = storeService.get((List<I>) bubbleIds);
        List<StoreEntry2<T>> cacheEntries = new ArrayList<StoreEntry2<T>>(bubbleObjects.size());
        for (Iterator<T> bubbleObjectIterator = bubbleObjects.iterator(); bubbleObjectIterator.hasNext();) {
            T bubbleObject = bubbleObjectIterator.next();
            StoreEntry2<T> newEntry = new StoreEntry2<T>(bubbleObject);
            StoreEntry2<T> cacheEntry = storeCache.register(newEntry);
            cacheEntries.add(cacheEntry);
        }
        return cacheEntries;
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> register(T bubbleObject) {
        return storeCache.register(new StoreEntry2<T>(bubbleObject));
    }
}
