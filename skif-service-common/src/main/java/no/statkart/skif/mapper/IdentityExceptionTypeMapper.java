package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;


/**
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public class IdentityExceptionTypeMapper<T extends Throwable> extends AbstractTypeMapper<T, T> {
    private Mapping mapping;

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public IdentityExceptionTypeMapper(Class<T> exceptionClass) {
        super(exceptionClass, exceptionClass);
    }

    @Override
    protected T getInitialDomainObject(T source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return source;
    }

    @Override
    protected T getInitialWsapiObject(T source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return source;
    }
}
