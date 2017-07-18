package no.statkart.skif.store.multikobling;

import no.statkart.skif.exception.ConfigurationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory som brukes til å lage koblinger av type {@code K}. Factoryen bruker refelection til å finne frem til
 * hvilken constructor som skal brukes. Implementasjonen antar at Koblingklassen som angis har nettopp en konstructor
 * med 2 argumenter og at den er den som skal brukes.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DefaultKoblingFactory<R,V, K extends Kobling<R,V>> implements KoblingFactory<R,V,K> {
    private static final long serialVersionUID = 1L;

    private Class<K> koblingClass;

    /** Konstruktør som brukes for opprettelse av Kobling objekter. Må være transient siden konstruktører ikke kan serialiseres */
    private transient Constructor<K> constructor;

    @SuppressWarnings("unchecked")
    public DefaultKoblingFactory(Class<K> koblingClass) {
        this.koblingClass = koblingClass;
        this.constructor = findConstructor(koblingClass);
    }

    @SuppressWarnings("unchecked")
    private Constructor<K> findConstructor(Class<K> koblingClass) {
        for (Constructor<?> c : koblingClass.getConstructors()) {
            if (c.getParameterTypes().length==2) {
                return (Constructor<K>) c;
            }
        }
        throw new ConfigurationException("No matching constructor for " + koblingClass);
    }

    @Override
    public K create(R rolle, V value) {
        try {
            return constructor.newInstance(rolle, value);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigurationException(e);
        } catch (InvocationTargetException e) {
            throw new ConfigurationException(e.getTargetException());
        }
    }

    public static <R,V, K extends Kobling<R,V>> KoblingFactory<R, V, K> create(Class<K> c) {
        return new DefaultKoblingFactory<>(c);
    }

    /**
     *  Setter konstruktør ved deserialisering
     */
    private Object readResolve() {
        constructor = findConstructor(koblingClass);
        return this;
    }
}
