package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.annotation.WSServiceChain;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Adapter proxy som adapterer domain interface T til webservice interface A ved å mappe metoder med samme navn til hverandre og transformere
 * argumentene og resultatet vha et {@code map} objekt
 * <p>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Mapperen får som rolle å holde styr på evt wrapping av exceptions. Et eksempel kan være å wrappe alle ikke skif exceptions i en {@link no.statkart.skif.exception.ImplementationException}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class W2DAdapterProxyHandler<T, A> extends AdapterProxyHandler<T, A> {

    protected final Mapping map;

    /**
     * Optional mapping for exceptions. Can be <tt>null</tt> if not set.
     */
    @Inject(optional = true)
    @Nullable
    final protected ExceptionMapping exceptionMapping;


    @Inject()
    public W2DAdapterProxyHandler(@WSServiceChain A adaptee, Mapping map) {
        this(adaptee, map, null);
    }

    public W2DAdapterProxyHandler(A adaptee, Mapping map, ExceptionMapping exceptionMapping) {
        super(adaptee);
        this.map = map;
        this.exceptionMapping = exceptionMapping;
    }

    W2DAdapterProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler, Mapping map, ExceptionMapping exceptionMapping) {
        super(adapteeClass, handler);
        this.map = map;
        this.exceptionMapping = exceptionMapping;
    }

    @Override
    protected Method findMethod(Method method) throws NoSuchMethodException {
        Class[] interfaces = adapteeClass.isInterface() ? (new Class[]{adapteeClass}) : adapteeClass.getInterfaces();
        for (Class interfaceClass : interfaces) {
            for (Method m : interfaceClass.getMethods()) {
                if (m.getName().equals(method.getName())) {
                    return m;
                }
            }
        }
        throw new ImplementationException("No corresponding method in adaptee: " + method.toGenericString());
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Method toMethod = getMethod(method);
        try {
            Object result = mapArgsAndInvokeMethod(proxy, method, args, toMethod);
            return result;
        } catch (Throwable t) {
            throw t;
        }
    }

    protected Object mapArgsAndInvokeMethod(Object proxy, Method method, Object[] args, Method toMethod) throws Throwable {
        int length = toMethod.getParameterTypes().length;
        Object[] mappedArgs = mapArgs(args, method, toMethod, length);
        Object result = invokeMethodForMappedArgs(proxy, method, toMethod, mappedArgs);
        return result;
    }

    protected Object invokeMethodForMappedArgs(Object proxy, Method method, Method toMethod, Object[] mappedArgs) throws Throwable {
        try {
            Object result = adapteeRoot.invoke(proxy, toMethod, mappedArgs);
            return mapResult(method, toMethod, result);
        } catch (Throwable t) {
            if (exceptionMapping != null) {
                Throwable mappedException = exceptionMapping.d2w(t);
                throw mappedException;
            }
            throw t;
        }
    }

    protected Object mapResult(Method method, Method toMethod, Object result) {
        return map.d2w(result, toMethod.getGenericReturnType(), method.getGenericReturnType());
    }

    /**
     * Mapper argumenter i args slik at de kan brukes som inn-parametre til {@code doapiMethod}.
     * Det opprettes like mange parametre som det {@code doapiMethod} krever, men det mappes kun {@code length}
     * antall argumenter fra {@code args}. Metoden er laget slik fordi {@code wsapiMethod} kan ha en ekstra context
     * parameter i forhold til {@code doapiMethod}. Videre så kan {@code doapiMethod} også ha
     * parametre som mappes via context parameteren i {@code wsapiMethod}.
     */
    protected Object[] mapArgs(Object[] args, Method wsapiMethod, Method doapiMethod, int length) {
        Object[] mappedArgs;
        if (args != null) {
            mappedArgs = new Object[doapiMethod.getParameterTypes().length];
            Type[] toTypes = doapiMethod.getGenericParameterTypes();
            Type[] fromTypes = wsapiMethod.getGenericParameterTypes();
            for (int i = 0; i < length; i++) {
                mappedArgs[i] = map.w2d(args[i], fromTypes[i], toTypes[i]);
            }
            return mappedArgs;
        } else {
            return null;
        }
    }
}
