package no.statkart.skif;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.Type;

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
            return (T) classForName(className).newInstance();
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
     *   SkifUtil.getType(new TypeToken<Simmple<?>>(){})
     * </pre>
     * Ovenstående vil returnere Class<Simple<?>>.
     *
     * @since 2.4
     */
    public static <E> Class<E> getType(TypeToken<E> type) {
        return (Class<E>)type.getRawType();
    }

}
