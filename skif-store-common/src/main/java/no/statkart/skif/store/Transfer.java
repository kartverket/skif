package no.statkart.skif.store;

import java.io.Serializable;
import java.util.*;

/**
 * Transfer-wrapperklasse.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 *
 * @param <T>    Datatypen transfer er wrapper for
 */
public class Transfer<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<BubbleId, BubbleObject> bubbleObjects = new LinkedHashMap<BubbleId, BubbleObject>();
    private final T result;

    public Transfer(T result) {
        this.result = result;
    }

    public Transfer(T result, Iterable<? extends BubbleObject> bubbleObjects) {
        this(result);
        addAll(bubbleObjects);
    }

    public void add(BubbleObject bubbleObject) {
        bubbleObjects.put(bubbleObject.getBubbleId(), bubbleObject);
    }

    public void addAll(Iterable<? extends BubbleObject> c) {
        for (BubbleObject bubbleObject : c) {
            add(bubbleObject);
        }
    }

    public Map<BubbleId, BubbleObject> getBubbleObjects() {
        return Collections.unmodifiableMap(bubbleObjects);
    }

    /**
     * Get an object from this transfer.
     *
     * @param id bubble id for the object
     * @return a bubble object
     */
    public final BubbleObject getObject(BubbleId id) {
        return bubbleObjects.get(id);
    }

    public T getResult() {
        return result;
    }
}
