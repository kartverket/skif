package no.statkart.skif.store2;

import com.google.inject.Inject;
import no.statkart.skif.exception2.ObjectNotFoundException2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * HashMap basert Store som ikke er trådsikker.
 * @author Henrik Fredholm
 */
public class HashStorePersister2<T extends BubbleObject2, I extends BubbleId2<? extends T>> implements StorePersister2<T, I> {
    final private Map<I, T> storeMap;

    @Inject
    public HashStorePersister2(Map<I, T> store) {
        this.storeMap = store;
    }

    public T get(I bubbleId) {
        T object = storeMap.get(bubbleId);
        if (object==null) {
            throw new ObjectNotFoundException2(bubbleId);
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

    public void evict(BubbleId2 bubbleId) {
        // No-op
    }

    public void evictAll() {
        // No-op
    }
}
