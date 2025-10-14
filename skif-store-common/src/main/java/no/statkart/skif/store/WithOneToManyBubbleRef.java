package no.statkart.skif.store;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface WithOneToManyBubbleRef {
    void preFlush(BiFunction<Class<?>, Serializable, Object> loader);
    void postLoad();
}
