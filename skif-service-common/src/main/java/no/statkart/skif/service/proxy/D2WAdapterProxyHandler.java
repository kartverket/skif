package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;

import javax.annotation.Nullable;
import javax.xml.ws.WebFault;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Adapter proxy som adapterer domain interface T til webservice interface A ved å mappe metoder med samme navn til hverandre og transformere
 * argumentene og resultatet vha et mapping2 objekt
 * <p/>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Alle @{Exception}s annotert med {@WebFault} blir mappet over til korresponderende exceptions ihht til mapper. All andre exceptions blir fanget og wrappet til
 * {@link ImplementationException}.
 *
 * @author Henrik Fredholm
 * @NotTheadSafe
 * @since 2.0
 */
public class D2WAdapterProxyHandler<T, A> extends AdapterProxyHandler<T, A> {

    final Mapping map;

    /**
     * Optional mapping2 for exceptions. Can be <tt>null</tt> if not set.
     */
    @Inject(optional = true)
    @Nullable
    final ExceptionMapping exceptionMapping;


    @Inject()
    public D2WAdapterProxyHandler(A adaptee, Mapping map) {
        this(adaptee, map, null);
    }

    public D2WAdapterProxyHandler(A adaptee, Mapping map, ExceptionMapping exceptionMapping) {
        super(adaptee);
        this.map = map;
        this.exceptionMapping = exceptionMapping;
    }

    D2WAdapterProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler, Mapping map, ExceptionMapping exceptionMapping) {
        super(adapteeClass, handler);
        this.map = map;
        this.exceptionMapping = exceptionMapping;
    }

    @Override
    protected Method findMethod(Method method) throws NoSuchMethodException {
        for (Class interfaceClass : adapteeClass.getInterfaces()) {
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
        Method m = getMethod(method);

        Object[] mappedArgs = mapArgs(args, method, m);

        try {
            Object result = adapteeRoot.invoke(proxy, m, mappedArgs);
            return map.w2d(result, method.getGenericReturnType());
        } catch (Throwable t) {
            if (exceptionMapping != null) {
                //forventer kun exceptions definert for webservice api. Disse er da annotert med @WebFault
                if (t.getClass().getAnnotation(WebFault.class) != null) {
                    Throwable mappedException = exceptionMapping.w2d(t);
                    throw mappedException;
                }
            }
            throw t;
        }
    }

    protected Object[] mapArgs(Object[] args, Method method, Method m) {
        if (args != null) {
            Object[] mappedArgs = new Object[args.length];
            Type[] ts = m.getGenericParameterTypes();
            for (int i = 0; i < args.length; i++) {
                mappedArgs[i] = map.d2w(args[i], ts[i]);
            }
            return mappedArgs;
        } else {
            return null;
        }
    }
}
