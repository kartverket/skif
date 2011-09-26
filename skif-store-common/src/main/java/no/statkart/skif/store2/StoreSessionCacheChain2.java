package no.statkart.skif.store2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Loads BubbleObjects into into the StoreCache2
 *
 * @author Henrik Fredholm
 */
public class StoreSessionCacheChain2 implements StoreSessionReadChain2, StoreSessionUpdateChain2 {
    protected StoreCache2 storeCache;
    protected StoreSessionReadChain2 nextInReadChain;
    protected StoreSessionUpdateChain2 nextInWriteChain;
    private Store2 store;

    @Override
    public void clear() {
        // Ignore
    }

    @Override
    public void init(StoreCache2 storeCache) {
        this.storeCache = storeCache;
        store = storeCache.getStore();
    }

    @Override
    public StoreSessionReadChain2 setNextInReadChain(StoreSessionReadChain2 next) {
        this.nextInReadChain = next;
        return next;
    }

    @Override
    public StoreSessionUpdateChain2 setNextInWriteChain(StoreSessionUpdateChain2 next) {
        this.nextInWriteChain = next;
        return next;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId) {
        StoreEntry2<T> newEntry = nextInReadChain.get(bubbleId);
        StoreEntry2<T> cacheEntry = storeCache.register(newEntry);
        return cacheEntry;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> get(Collection<I> bubbleIds) {
        Collection<StoreEntry2<T>> newEntries = nextInReadChain.get(bubbleIds);
        List<StoreEntry2<T>> cacheEntries;

        if (newEntries instanceof ArrayList) {
            cacheEntries = (List<StoreEntry2<T>>) newEntries;
        } else {
            cacheEntries = new ArrayList<StoreEntry2<T>>(newEntries.size());
            Collections.fill(cacheEntries, null);
        }

        int i = 0;
        for (StoreEntry2<T> newEntry : newEntries) {
            StoreEntry2<T> cacheEntry = storeCache.register(newEntry);
            cacheEntries.set(i++, cacheEntry);
        }
        return cacheEntries;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> lock(I bubbleId) {
        StoreEntry2<T> newEntry = nextInWriteChain.lock(bubbleId);
        StoreEntry2<T> cacheEntry = storeCache.registerLocked(newEntry);
        return cacheEntry;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> lock(Collection<I> bubbleIds) {
        Collection<StoreEntry2<T>> newEntries = nextInWriteChain.lock(bubbleIds);
        List<StoreEntry2<T>> cacheEntries;

        if (newEntries instanceof ArrayList) {
            cacheEntries = (List<StoreEntry2<T>>) newEntries;
        } else {
            cacheEntries = new ArrayList<StoreEntry2<T>>(newEntries.size());
            Collections.fill(cacheEntries, null);
        }

        int i = 0;
        for (StoreEntry2<T> newEntry : newEntries) {
            StoreEntry2<T> cacheEntry = storeCache.registerLocked(newEntry);
            cacheEntries.set(i++, cacheEntry);
        }
        return cacheEntries;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean isLocked(I bubbleId) {
        boolean result;
        StoreEntry2<T> storeEntry = storeCache.get(bubbleId);

        if (storeEntry !=null) {
            result = storeEntry.isLocked();
        } else {
            result = nextInWriteChain.isLocked(bubbleId);
        }
        return result;
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> register(T bubbleObject) {
        return storeCache.register(new StoreEntry2<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerLocked(T bubbleObject) {
        return storeCache.registerLocked(new StoreEntry2<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerNew(T bubbleObject) {
        return storeCache.registerNew(new StoreEntry2<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerUpdated(T bubbleObject) {
        return storeCache.registerUpdated(new StoreEntry2<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> void registerDeleted(I bubbleId) {
        storeCache.registerDeleted(new StoreEntry2<T>(bubbleId));
    }
}
