package no.statkart.skif.service.proxy;

import com.google.inject.Provider;
import no.statkart.skif.exception.ConfigurationException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter proxy som adaptere interface T til A. Default implementasjon
 * for {@link #invoke} mapper metoder med samme navn og argumenter til hverandre.
 * <p/>
 *
 * @author Henrik Fredholm
 */
public class AdapterProxyHandler<T, A> extends TerminatingProxyHandler<T> {
    final protected ProxyHandler<A> adapteeRoot;
    final protected Map<Method, Method> methodCache = new HashMap<Method, Method>();
    final protected Class adapteeClass;

    public AdapterProxyHandler(A adaptee) {
        adapteeClass = adaptee.getClass();
        adapteeRoot = new InvokeViaInstanceProxyHandler<A>(adaptee);
    }

    public AdapterProxyHandler(Provider<A> adaptee, Class<A> adapteeClass) {
        this.adapteeClass = adapteeClass;
        adapteeRoot = new InvokeViaProviderProxyHandler<A>(adaptee);
    }

    public AdapterProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler) {
        this.adapteeClass = adapteeClass;
        adapteeRoot = handler;
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Method m = getMethod(method);
        return adapteeRoot.invoke(proxy, m, args);
    }

    protected Method getMethod(Method method) {
        Method m = methodCache.get(method);
        if (m == null) {
            try {
                m = findMethod(method);
            } catch (NoSuchMethodException e) {
                throw new ConfigurationException(e);
            }
            methodCache.put(method, m);
        }
        return m;
    }

    protected Method findMethod(Method method) throws NoSuchMethodException {
        return adapteeClass.getMethod(method.getName(), method.getParameterTypes());
    }
}