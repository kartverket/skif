package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.util.ResourceUtils;

import java.util.*;

/**
 * HashMap basert Store som ikke er trådsikker.
 *
 * @author Henrik Fredholm
 */
public class HashStorePersister<T extends BubbleObject, I extends BubbleId<? extends T>> implements StorePersister<T, I> {
    final private Map<I, T> storeMap;

    @Inject
    public HashStorePersister(Map<I, T> store) {
        this.storeMap = store;
    }

    public T get(I bubbleId) {
        T object = storeMap.get(bubbleId);
        if (object == null) {
            throw new ObjectNotFoundException(bubbleId);
        }
        return object;
    }

    @Override
    public Map<SnapshotVersion, Collection<? extends T>> get(Map<SnapshotVersion, Collection<? extends I>> bubbleIdsForSnapshotMap) {
        Map<SnapshotVersion, Collection<? extends T>> result = new HashMap<SnapshotVersion, Collection<? extends T>>();
        for (Map.Entry<SnapshotVersion, Collection<? extends I>> snapshotVersionListEntry : bubbleIdsForSnapshotMap.entrySet()) {
            SnapshotVersion snapshotVersion = snapshotVersionListEntry.getKey();
            Collection<? extends I> bubbleIds = snapshotVersionListEntry.getValue();
            List<T> bubbles = new ArrayList<T>();
            for (I id : bubbleIds) {
                T object = get(id);
                bubbles.add(object);
            }
            result.put(snapshotVersion, bubbles);
        }
        return result;
    }

    public void evict(BubbleId bubbleId) {
        // No-op
    }

    public void evictAll() {
        // No-op
    }
}
