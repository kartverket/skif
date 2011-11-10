package no.statkart.skif.mapper;

import no.statkart.skif.exception.ApplicationException;
import no.statkart.skif.exception.SkifException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory som oppretter alle typer kjente {@link SkifException}s
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class ExceptionObjectFactory extends DefaultObjectFactory {

    @Override
    public <S, T> T getInitialObject(S source, Class<T> targetClass) throws InstantiationException, IllegalAccessException {
        if (SkifException.class.isAssignableFrom(targetClass)) {
            return (T) createSkifException((Throwable) source, (Class<SkifException>) targetClass);
        }

        return super.getInitialObject(source, targetClass);
    }


    static <S extends Throwable, T extends SkifException> T createSkifException(S source, Class<T> targetClass) throws InstantiationException, IllegalAccessException {
        try {
            Constructor<? extends SkifException> constructor;
            if (ApplicationException.class.isAssignableFrom(targetClass)) {
                constructor = targetClass.getDeclaredConstructor(String.class, String.class, String.class, Throwable.class);
                return (T) constructor.newInstance(null, null, source.getMessage(), source.getCause());
            } else {
                constructor = targetClass.getDeclaredConstructor(String.class, Throwable.class);
                return (T) constructor.newInstance(source.getMessage(), source.getCause());
            }

        } catch (NoSuchMethodException e) {
            throw new MappingException("No known instantiation for exception class: " + targetClass);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error in instantiating class " + targetClass.getName(), e);
        }
    }
}
