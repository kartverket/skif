package no.statkart.skif.service.proxy;


import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@code ProxyHandler} for service {@code S} som sender kallet videre via en {@code Provider}
 * for instans av type {@code S}.
 * <p>
 * Denne proxy handler gjør at {@code S} først blir opprettet i det øyeblikk at en metode på
 * {@code S} utføres. Dersom denne indireksjon ikke trengs kan
 * {@link InvokeViaInstanceProxyHandler} med fordel brukes i stedet.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class InvokeViaProviderProxyHandler<S> extends TerminatingProxyHandler<S> {
    final protected Provider<S> provider;

    public InvokeViaProviderProxyHandler(Provider<S> provider) {
        this.provider = provider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args)  throws Throwable{
        try {
            S instance = provider.get();
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalArgumentException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }
}
