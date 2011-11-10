package no.statkart.skif.service.proxy;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;

import java.lang.reflect.Method;

/**
 * Fanger RuntimeExceptions som ikke er av type SkifException og wrapper dem i en ImplementationException
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RuntimeExceptionProxyHandler<S> extends ChainedProxyHandler<S> {
    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return chained.invoke(proxy, method, args);
        } catch (SkifException t) {
            throw t;
        } catch (Throwable t) {
            throw new ImplementationException(t.getMessage(), t);
        }
    }
}
