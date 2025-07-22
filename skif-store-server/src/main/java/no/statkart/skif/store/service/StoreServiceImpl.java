package no.statkart.skif.store.service;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreServiceImpl implements StoreService {
    @Inject
    protected Store store;

    @Override
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
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
        return store.getVersions(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        return store.getVersionsForList(ids, start, end);
    }
}
