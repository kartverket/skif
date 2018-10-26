package no.statkart.skif.store.endringslogg;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import no.statkart.skif.inject.Holder;
import no.statkart.skif.inject.HolderImpl;
import no.statkart.skif.store.BubbleObject;

import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;


/**
 * Deklarativ konfigurasjon for {@link AbstractEndringManager}
 *
 * @author Leif Lislegård
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public abstract class AbstractEndringManagerConfiguration<E extends AbstractEndring> implements EndringManagerConfiguration<E> {
    final BiMap<Class<? extends E>, Class<? extends BubbleObject>> biMapEndring2Domain;

    private final ConcurrentMap<Class<? extends BubbleObject>, Holder<Class<? extends E>>> cache = Maps.newConcurrentMap();

    protected AbstractEndringManagerConfiguration(BiMap<Class<? extends E>, Class<? extends BubbleObject>> biMapEndring2Domain) {
        this.biMapEndring2Domain = biMapEndring2Domain;
    }

    @Override
    public final Class<? extends BubbleObject> getDomainklasse(Class<? extends E> endringklasse) {
        return biMapEndring2Domain.get(endringklasse);
    }

    @Override
    public final Class<? extends BubbleObject> getDomainklasseNullSafe(Class<? extends E> endringklasse) {
        return requireNonNull(getDomainklasse(endringklasse), () -> String.format("Fant ingen domeneklasse hørende til endringsklasse %s", endringklasse.getName()));
    }

    @Override
    public final Class<? extends E> getEndringsklasse(Class<? extends BubbleObject> domainklasse) {
        return biMapEndring2Domain.inverse().get(domainklasse);

    }

    @Override
    public final Class<? extends E> getEndringsklasseNullSafe(Class<? extends BubbleObject> domainklasse) {
        return requireNonNull(getEndringsklasse(domainklasse), () -> String.format("Domainklasse %s kan ikke brukes som filter", domainklasse.getSimpleName()));
    }

    @Override
    public Class<? extends E> findEndringClass(Class<? extends BubbleObject> domainklasse) {
        Holder<Class<? extends E>> endringsklasseHolder = cache.get(domainklasse);
        if (endringsklasseHolder == null) {
            endringsklasseHolder = new HolderImpl<>();
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


