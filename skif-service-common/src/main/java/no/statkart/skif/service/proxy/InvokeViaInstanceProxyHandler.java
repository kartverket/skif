package no.statkart.skif.service.proxy;


import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@code ProxyHandler} for service {@code S} som sender kallet videre til instans av type {@code S}.
 * <p>
 * Denne proxy handler krever at {@code S} blir opprettet før eller i det øyeblikk at proxy handleren
 * blir opprettet. Dersom opprettelsen av {@code S} først skal skje i det at en metode på S utføres
 * bør {@link InvokeViaProviderProxyHandler} brukes i stedet
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class InvokeViaInstanceProxyHandler<S> extends TerminatingProxyHandler<S> {
    final protected S instance;

    public InvokeViaInstanceProxyHandler(S instance) {
        this.instance = instance;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args)  throws Throwable{
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalArgumentException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public S buildProxy(Class<S> type) {
        return type.cast(instance);
    }
}
