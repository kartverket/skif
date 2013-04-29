package no.statkart.skif.store.service;

import com.google.inject.Inject;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreServiceImpl implements StoreService {
    @Inject
    protected Store store;

    @Inject
    protected VersionFinder versionFinder;

    @Inject
    protected ServiceRequestContext serviceRequestContext;

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I id) {
        final T bubbleObject = store.get(id);
        store.ensureFullyLoaded(bubbleObject);
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
        final Collection<T> bubbleObjects = store.get(ids);
        // TODO: Opptimaliser for bulk
        for (T bubbleObject : bubbleObjects) {
            store.ensureFullyLoaded(bubbleObject);
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
        final Collection<T> bubbleObjects = store.getIgnoreMissing(ids);
        // TODO: Opptimaliser for bulk
        for (T bubbleObject : bubbleObjects) {
            store.ensureFullyLoaded(bubbleObject);
        }
        return bubbleObjects;
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return versionFinder.findBubbleIdsForInterval(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        Map<I, List<I>> retur = new HashMap<I, List<I>>();
        for (I id : ids) {
            retur.put(id, versionFinder.findBubbleIdsForInterval(id, start, end));
        }
        return retur;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I id) {
        return store.isLocked(id);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I id) throws LockedException {
        final T bubbleObject = store.lock(id);
        store.ensureFullyLoaded(bubbleObject);
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I id) {
        store.unlock(id);
    }

}
