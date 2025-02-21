package no.statkart.skif;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifUtil {
    public static <T> T newInstance(Class<T> aClass) {
        try {
            return aClass.newInstance();
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }

    public static <T> T newInstance(String className) {
        try {
            return SkifUtil.<T>classForName(className).newInstance();
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }

    public static <T> Class<? extends T> classForName(String className) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }

    public static <S, T extends S> T cast(Class<T> aClass, S object) {
        try {
            return aClass.cast(object);
        } catch (ClassCastException e) {
            throw new ClassCastException("Could not cast object of class " + object.getClass().getName() + " to class " + aClass.getName());
        }
    }

    public static <T extends TypeLiteral> T typeLiteral(Type rawType, Type... types) {
        return (T) TypeLiteral.get(Types.newParameterizedType(rawType, types));
    }

    /**
     * Hjelpemetode for å hente ut Class for unbounded typer.
     *
     * <p>Eksempel på bruk:
     * <pre>
     *   SkifUtil.getType(new TypeToken&lt;Simple&lt;?&gt;&gt;(){})
     * </pre>
     * Ovenstående vil returnere Class&lt;Simple&lt;?&gt;&gt;.
     *
     * @since 2.4
     */
    public static <E> Class<E> getType(TypeToken<E> type) {
        return (Class<E>)type.getRawType();
    }

    public static <T> T newInstance(String className, Object arg) {
        try {
            Class<? extends T> aClass = (Class<? extends T>) Class.forName(className);
            return aClass.getConstructor(arg.getClass()).newInstance(arg);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        } catch (NoSuchMethodException e) {
            throw new ImplementationException(e);
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }

    /**
     * Oppretter en HashMap dimensjonert til antall keys og legger inn {@code null} for hver key
     */
    public static <K, V> HashMap<K, V> newHashMapWithNullValues(Collection<? extends K> keys) {
        HashMap<K, V> map = Maps.newHashMapWithExpectedSize(keys.size());
        for (K k : keys) {
            map.put(k, null);
        }
        return map;
    }

    /**
     * Oppretter en HashMap dimensjonert til antall keys og legger inn et tomt sett for hver key
     */
    public static <K, E> HashMap<K, Set<E>> newHashMapWithEmptySetValues(Collection<? extends K> keys) {
        HashMap<K, Set<E>> map = Maps.newHashMapWithExpectedSize(keys.size());
        for (K k : keys) {
            map.put(k, Sets.<E>newHashSet());
        }
        return map;
    }
}
