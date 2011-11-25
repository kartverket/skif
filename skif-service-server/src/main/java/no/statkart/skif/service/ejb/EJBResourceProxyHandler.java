package no.statkart.skif.service.ejb;

import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.lang.reflect.Method;

/**
 * En EJBServiceChain ProxyHandler som har ansvar for å avslutte ressurser som har vært i bruk under servicekallet
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EJBResourceProxyHandler<S> extends ChainedProxyHandler<S> {

    protected abstract void beginService();
    protected abstract void completeService();
    protected abstract void abortService();

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            beginService();
            ProxyHandler<S> chained = getChained();
            Object result = chained.invoke(proxy, method, args);
            completeService();
            return result;
        } catch (Throwable e) {
            abortService();
            throw e;
        }
    }
}
