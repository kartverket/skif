package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;


/**
 * Factory for å få tak i objektinstanse det skal mappes til.
 * @author Henrik Fredholm
 */
public interface ObjectFactory {
    public <S, T> T getInitialObject(S source, Class<T> targetClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    public Mapping getMapping();
    public void setMapping(Mapping m);
}