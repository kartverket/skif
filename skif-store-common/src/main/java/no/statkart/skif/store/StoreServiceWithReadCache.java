package no.statkart.skif.store;

import com.google.common.collect.ImmutableMap;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoreServiceWithReadCache implements StoreService, LockService {
    private final StoreService storeService;
    private final LockService lockService;
    private final StoreClientReadCache readCache;

    public <T extends StoreService & LockService> StoreServiceWithReadCache(T storeLockService, StoreClientReadCache readCache) {
        this(storeLockService, storeLockService, readCache);
    }

    public StoreServiceWithReadCache(StoreService storeService, LockService lockService, StoreClientReadCache readCache) {
        this.storeService = storeService;
        this.lockService = lockService;
        this.readCache = Objects.requireNonNull(readCache, "readCache");
    }

    @Override
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
        if (id == null) return null;
        //noinspection unchecked
        T bubbleObject = readCache.get(id);
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
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersionsForList(ids, start, end);
    }

    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
        T lockedObject = lockService.lock(id);
        readCache.put(lockedObject);
        return lockedObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
        Collection<T> lockedObjects = lockService.lockForList(ids);
        readCache.putAll(lockedObjects);
        return lockedObjects;
    }

    @Override
    public <I extends BubbleId<?>> void unlock(I id) {
        lockService.unlock(id);
    }

    @Override
    public void unlockForList(Collection<? extends BubbleId<?>> ids) {
        lockService.unlockForList(ids);
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I id) {
        return lockService.isLocked(id);
    }
}
