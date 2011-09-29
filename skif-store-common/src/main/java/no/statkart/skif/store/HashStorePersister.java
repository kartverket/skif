package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * HashMap basert Store som ikke er trådsikker.
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
        if (object==null) {
            throw new ObjectNotFoundException(bubbleId);
        }
        return object;
    }


    public Collection<? extends T> get(Collection<? extends I> bubbleIds) {
        List<T> result = new ArrayList<T>();
        for (I id : bubbleIds) {
            T object = get(id);
            result.add(object);
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
