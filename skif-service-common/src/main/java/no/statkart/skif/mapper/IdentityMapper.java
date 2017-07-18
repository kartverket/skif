package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class IdentityMapper implements InvocationHandler {

    private final Mapping thisMapping;

    public IdentityMapper() {
        thisMapping = (Mapping) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Mapping.class}, this);
    }

    public Mapping getMapping() {
        return thisMapping;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args.length>=1) {
            return args[0];
        }
        // Invode built in method
        try {
            //noinspection UnnecessaryLocalVariable
            final Object result = method.invoke(this, args);
            return result;
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
