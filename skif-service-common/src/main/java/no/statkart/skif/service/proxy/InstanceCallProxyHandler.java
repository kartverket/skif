package no.statkart.skif.service.proxy;


import com.google.inject.Provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@code ProxyHandler} for service {@code S} som sender kallet videre til instans av type {@code S}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class InstanceCallProxyHandler<S> extends TerminatingProxyHandler<S> {
    final protected S instance;

    public InstanceCallProxyHandler(S instance) {
        this.instance = instance;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args)  throws Throwable{
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public S buildProxy(Class<S> type) {
        return type.cast(instance);
    }
}
