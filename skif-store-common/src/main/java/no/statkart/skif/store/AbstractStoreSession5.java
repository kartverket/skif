package no.statkart.skif.store;

import java.util.*;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSession5 implements WrappableStoreSession5 {
    protected final int level;
    protected final StoreCache5 storeCache;
    protected final Set<BubbleId<?>> modifiedByThisLevel= new LinkedHashSet<BubbleId<?>>(150);
    protected final Set<BubbleId<?>> lockedByThisLevel= new HashSet<BubbleId<?>>(150);

    protected AbstractStoreSession5(int level, StoreCache5 storeCache) {
        this.level = level;
        this.storeCache = storeCache;
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        StoreEntry5 entry = getEntry(level, bubbleId);
        return (T)entry.getBubbleObject(level);
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 getEntry(int level, I bubbleId) {
        StoreEntry5 entry = storeCache.get(bubbleId);
        if (entry == null) {
            entry = loadEntry(level, bubbleId);
        }
        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(Collection<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");

        ArrayList<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        ArrayList<I> orderedBubbleIds = new ArrayList<I>(bubbleIds.size());
        Collections.fill(bubbleObjects, null);
        ArrayList<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            orderedBubbleIds.add(bubbleId);
            final StoreEntry5 storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null && storeEntry.getBubbleObject(level)!= null) {
                bubbleObjects.add((T)storeEntry.getBubbleObject(level));
            } else {
                bubbleObjects.add(null);  // null er plassholder
                if (missingBubbleIds == null) {
                    missingBubbleIds = new ArrayList<I>(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        // Sjekk om alle ble funnet
        if (missingBubbleIds!=null) {
            final Collection<StoreEntry5> storeEntries = getEntries(level, missingBubbleIds);
            final Map<BubbleId<?>, StoreEntry5> storeEntryMap = new HashMap<BubbleId<?>, StoreEntry5>(storeEntries.size());
            for (StoreEntry5 storeEntry : storeEntries) {
                storeEntryMap.put(storeEntry.getId(), storeEntry);
            }
            for (int i = 0; i < bubbleObjects.size(); i++) {
                if (bubbleObjects.get(i)==null) {
                    final StoreEntry5 storeEntry = storeEntryMap.get(orderedBubbleIds.get(i));
                    bubbleObjects.set(i, (T)storeEntry.getBubbleObject(level));
                }
            }
        }
        return bubbleObjects;
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId) {
        return evictEntry(level, bubbleId);
    }

    @Override
    public final <T extends BubbleObject> T register(T bubbleObject) {
        StoreEntry5 entry = registerEntry(level, bubbleObject);
        return (T) entry.getBubbleObject(level);
    }

    @Override
    public final <T extends BubbleObject> void insert(T bubbleObject) {
        insertEntry(level, bubbleObject);
    }

    @Override
    public final <T extends BubbleObject> void update(T bubbleObject) {
        updateEntry(level, bubbleObject);
    }

    @Override
    public final <T extends BubbleObject> void delete(T bubbleObject) {
        deleteEntry(level, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I bubbleId) {
        StoreEntry5 entry = lockEntry(level, bubbleId);
        return (T)entry.getBubbleObject(level);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
