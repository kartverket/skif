package no.statkart.skif.store;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Loads BubbleObjects into into the StoreCache
 *
 * @author Henrik Fredholm
 */
public class StoreSessionCacheChain implements StoreReadChain, StoreUpdateChain {
    protected StoreCache storeCache;
    protected StoreReadChain nextInReadChain;
    protected StoreUpdateChain nextInWriteChain;
    private Store store;
    @Override
    public void clear() {
        // Ignore
    }

    @Override
    public void init(StoreCache storeCache) {
        this.storeCache = storeCache;
        store = storeCache.getStore();
    }

    @Override
    public StoreReadChain setNextInReadChain(StoreReadChain next) {
        this.nextInReadChain = next;
        return next;
    }

    @Override
    public StoreUpdateChain setNextInWriteChain(StoreUpdateChain next) {
        this.nextInWriteChain = next;
        return next;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        StoreEntry<T> newEntry = nextInReadChain.get(bubbleId);
        StoreEntry<T> cacheEntry = storeCache.register(newEntry);
        return cacheEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        Collection<StoreEntry<T>> newEntries = nextInReadChain.get(bubbleIds);
        List<StoreEntry<T>> cacheEntries;

        if (newEntries instanceof ArrayList) {
            cacheEntries = (List<StoreEntry<T>>) newEntries;
        } else {
            cacheEntries = new ArrayList<StoreEntry<T>>(newEntries.size());
            Collections.fill(cacheEntries, null);
        }

        int i = 0;
        for (StoreEntry<T> newEntry : newEntries) {
            StoreEntry<T> cacheEntry = storeCache.register(newEntry);
            cacheEntries.set(i++, cacheEntry);
        }
        return cacheEntries;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> lock(I bubbleId) {
        StoreEntry<T> newEntry = nextInWriteChain.lock(bubbleId);
        StoreEntry<T> cacheEntry = storeCache.registerLocked(newEntry);
        return cacheEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds) {
        Collection<StoreEntry<T>> newEntries = nextInWriteChain.lock(bubbleIds);
        List<StoreEntry<T>> cacheEntries;

        if (newEntries instanceof ArrayList) {
            cacheEntries = (List<StoreEntry<T>>) newEntries;
        } else {
            cacheEntries = new ArrayList<StoreEntry<T>>(newEntries.size());
            Collections.fill(cacheEntries, null);
        }

        int i = 0;
        for (StoreEntry<T> newEntry : newEntries) {
            StoreEntry<T> cacheEntry = storeCache.registerLocked(newEntry);
            cacheEntries.set(i++, cacheEntry);
        }
        return cacheEntries;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        boolean result;
        StoreEntry<T> storeEntry = storeCache.get(bubbleId);

        if (storeEntry !=null) {
            result = storeEntry.isLocked();
        } else {
            result = nextInWriteChain.isLocked(bubbleId);
        }
        return result;
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject) {
        return storeCache.register(new StoreEntry<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerLocked(T bubbleObject) {
        return storeCache.registerLocked(new StoreEntry<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerNew(T bubbleObject) {
        return storeCache.registerNew(new StoreEntry<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerUpdated(T bubbleObject) {
        return storeCache.registerUpdated(new StoreEntry<T>(bubbleObject));
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerDeleted(I bubbleId) {
        storeCache.registerDeleted(new StoreEntry<T>(bubbleId));
    }
}
