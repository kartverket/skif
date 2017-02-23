package no.statkart.skif.storetest.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.service.StoreService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class StoreServiceTestHelper {
    public static StoreService createStoreService(final Map<BubbleId<?>, BubbleObject> map) {
        return new StoreService() {
            @Override
            public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
                BubbleObject bubbleObject = map.get(id);
                if (bubbleObject==null) throw new ObjectNotFoundException(id);
                return (T) bubbleObject;
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
                List<T> result = Lists.newArrayList();
                for (I id : ids) {
                    T bubbleObject = getObject(id);
                    result.add(bubbleObject);
                }
                return result;
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
                List<T> result = Lists.newArrayList();
                for (I id : ids) {
                    T bubbleObject = (T) map.get(id);
                    if (bubbleObject != null) {
                        result.add(bubbleObject);
                    }
                }
                return result;
            }

            @Override
            public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
                throw  new NotImplementedException();
            }

            @Override
            public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
                throw  new NotImplementedException();
            }

            @Override
            public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
                return getObject(id);
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
                return getObjects(ids);
            }

            @Override
            public <I extends BubbleId<?>> void unlock(I id) {
                // no-op
            }

            @Override
            public void unlockForList(Collection<? extends BubbleId<?>> ids) {
                // no-op
            }

            @Override
            public <I extends BubbleId<?>> boolean isLocked(I id) {
                throw  new NotImplementedException();
            }
        };
    }

    public static StoreService createStoreServiceWithNoObjects() {
        return createStoreServiceWithObjects();
    }

    public static StoreService createStoreServiceWithObjects(BubbleObject... objects) {
        HashMap<BubbleId<?>, BubbleObject> map = Maps.newHashMap();
        for (BubbleObject expectedObject : objects) {
            map.put(expectedObject.getId(), expectedObject);
        }
        return createStoreService(map);
    }
}
