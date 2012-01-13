package no.statkart.skif.store5;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSession implements WrappableStoreSession {
    protected final int level;
    protected final StoreCache storeCache;
    protected final Set<BubbleId<?>> modifiedByThisLevel= new LinkedHashSet<BubbleId<?>>(150);
    protected final Set<BubbleId<?>> lockedByThisLevel= new HashSet<BubbleId<?>>(150);

    protected AbstractStoreSession(int level, StoreCache storeCache) {
        this.level = level;
        this.storeCache = storeCache;
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        StoreEntry entry = getEntry(level, bubbleId);
        return (T)entry.getBubbleObject(level);
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry getEntry(int level, I bubbleId) {
        StoreEntry entry = storeCache.get(bubbleId);
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId) {
        return evictEntry(level, bubbleId);
    }

    @Override
    public final <T extends BubbleObject> T register(T bubbleObject) {
        StoreEntry entry = registerEntry(level, bubbleObject);
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
        StoreEntry entry = lockEntry(level, bubbleId);
        return (T)entry.getBubbleObject(level);
    }
}
