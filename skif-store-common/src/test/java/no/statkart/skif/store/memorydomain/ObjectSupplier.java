package no.statkart.skif.store.memorydomain;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectSupplier implements Function<BubbleId<?>, BubbleObject> {
    private final Map<String, BubbleObject> objectMap = new HashMap<>();

    public ObjectSupplier(BubbleObject... objects) {
        for (BubbleObject object : objects) {
            objectMap.put(object.getId().toString(), object);
            objectMap.put(object.getId().asBase().toString(), object);
        }
    }

    @Override
    public BubbleObject apply(BubbleId<?> bubbleId) {
        return objectMap.get(bubbleId.toString());
    }
}
