package no.statkart.skif.service.proxy;


import com.google.inject.Provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@code ProxyHandler} for service provider for {@code S} som sender kallet videre til instans av type {@code S}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ProviderCallProxyHandler<S> extends TerminatingProxyHandler<S> {
    final protected Provider<S> provider;

    public ProviderCallProxyHandler(Provider<S> provider) {
        this.provider = provider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args)  throws Throwable{
        S instance = provider.get();
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
}
