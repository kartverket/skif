package no.statkart.skif.store;

import com.google.common.collect.ImmutableMap;
import no.statkart.skif.store.service.StoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class StoreServiceWithReadCache implements StoreService {
    private final StoreService storeService;
    private final StoreClientReadCache readCache;

    public StoreServiceWithReadCache(StoreService storeService, StoreClientReadCache readCache) {
        this.storeService = storeService;
        this.readCache = checkNotNull(readCache, "readCache");
    }

    @Override
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
        if (id == null) return null;
        //noinspection unchecked
        T bubbleObject = (T) readCache.get(id);
        if (bubbleObject == null) {
            bubbleObject = storeService.getObject(id);
            if (bubbleObject != null) {
                readCache.put(bubbleObject);
            }
        }
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
        List<T> result = new ArrayList<>(ids.size());
        List<I> notInCache = new ArrayList<>(ids.size());
        getFromReadCache(ids, result, notInCache);
        if (!notInCache.isEmpty()) {
            Collection<T> objects = storeService.getObjects(notInCache);
            readCache.putAll(objects);
            result.addAll(objects);
        }
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
        List<T> result = new ArrayList<>(ids.size());
        List<I> notInCache = new ArrayList<>(ids.size());
        getFromReadCache(ids, result, notInCache);
        if (!notInCache.isEmpty()) {
            Collection<T> objects = storeService.getObjectsIgnoreMissing(notInCache);
            readCache.putAll(objects);
            result.addAll(objects);
        }
        return result;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> void getFromReadCache(Collection<I> ids, List<T> result, List<I> notInCache) {
        ImmutableMap<BubbleId<?>, BubbleObject> found = readCache.getAll(ids);
        for (I id : ids) {
            BubbleObject bubbleObject = found.get(id);
            if (bubbleObject != null) {
                //noinspection unchecked
                result.add((T) bubbleObject);
            } else {
                notInCache.add(id);
            }
        }
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersions(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersionsForList(ids, start, end);
    }

    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
        T lockedObject = storeService.lock(id);
        readCache.put(lockedObject);
        return lockedObject;
    }

    @Override
    public <I extends BubbleId<?>> void unlock(I id) {
        storeService.unlock(id);
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I id) {
        return storeService.isLocked(id);
    }
}
