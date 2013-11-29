package no.statkart.skif.store.endringslogg;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import no.statkart.skif.inject.Holder;
import no.statkart.skif.inject.HolderImpl;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deklarativ konfigurasjon for {@link AbstractEndringManager}
 *
 * @author Leif Lislegård
 * @author Henrik Fredholm
 * @since 2.4
 */
public abstract class AbstractEndringManagerConfiguration<E extends AbstractEndring> {
    final BiMap<Class<? extends E>, Class<? extends BubbleObject>> biMapEndring2Domain;

    private final ConcurrentMap<Class<? extends BubbleObject>, Holder<Class<? extends E>>> cache = Maps.newConcurrentMap();

    protected AbstractEndringManagerConfiguration(BiMap<Class<? extends E>, Class<? extends BubbleObject>> biMapEndring2Domain) {
        this.biMapEndring2Domain = biMapEndring2Domain;
    }

    public final Class<? extends BubbleObject> getDomainklasse(Class<? extends E> endringklasse) {
        return biMapEndring2Domain.get(endringklasse);
    }

    public final Class<? extends BubbleObject> getDomainklasseNullSafe(Class<? extends E> endringklasse) {
        return checkNotNull(getDomainklasse(endringklasse), "Fant ingen domeneklasse hørende til endringsklasse %s", endringklasse.getName());
    }

    public final Class<? extends E> getEndringsklasse(Class<? extends BubbleObject> domainklasse) {
        return biMapEndring2Domain.inverse().get(domainklasse);

    }

    public final Class<? extends E> getEndringsklasseNullSafe(Class<? extends BubbleObject> domainklasse) {
        return checkNotNull(getEndringsklasse(domainklasse), "Domainklasse %s kan ikke brukes som filter", domainklasse.getSimpleName());
    }

    protected Class<? extends E> findEndringClass(Class<? extends BubbleObject> domainklasse) {
        Holder<Class<? extends E>> endringsklasseHolder = cache.get(domainklasse);
        if (endringsklasseHolder == null) {
            endringsklasseHolder = new HolderImpl<Class<? extends E>>();
            for (Class bubbleClass = domainklasse; bubbleClass != null; bubbleClass = bubbleClass.getSuperclass()) {
                //noinspection unchecked
                Class<? extends E> endringsklasse = getEndringsklasse(bubbleClass);
                if (endringsklasse != null) {
                    endringsklasseHolder.set(endringsklasse);
                    cache.putIfAbsent(domainklasse, endringsklasseHolder);
                    break;
                }
            }
        }
        return endringsklasseHolder.get();
    }
}


