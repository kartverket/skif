package no.statkart.skif.service.ejb;

import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;

/**
 * Enkel proxyhandler som teller antall ejb kall og feil.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class EJBCounterProxyHandler<S> extends ChainedProxyHandler<S> {
    private static long invocationCount;
    private static long errorCount;

    public static void resetCounters() {
        invocationCount = 0;
        errorCount = 0;

    }
    public static long getInvocationCount() {
        return invocationCount;
    }

    public static long getErrorCount() {
        return errorCount;
    }
    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            invocationCount++;
            Object result = chained.invoke(proxy, method, args);
            return result;
        } catch (Throwable e) {
            errorCount++;
            throw e;
        }
    }
}
