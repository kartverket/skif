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
 * argumentene og resultatet vha et mapping2 objekt
 * <p/>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Mapperen får som rolle å holde styr på evt wrapping av exceptions. Et eksempel kan være å wrappe alle ikke skif exceptions i en {@link no.statkart.skif.exception.ImplementationException}.
 *
 * @author Henrik Fredholm
 * @NotTheadSafe
 * @since 2.0
 */
public class W2DAdapterProxyHandler<T, A> extends AdapterProxyHandler<T, A> {

    final Mapping map;

    /**
     * Optional mapping for exceptions. Can be <tt>null</tt> if not set.
     */
    @Inject(optional = true)
    @Nullable
    final ExceptionMapping exceptionMapping;


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
        Method adapteeMethod = getMethod(method);
        Object[] mappedArgs;

        try {
            mappedArgs = mapArgs(args, adapteeMethod, method);
            Object result = adapteeRoot.invoke(proxy, adapteeMethod, mappedArgs);
            final Object wsResult = map.d2w(result, method.getGenericReturnType());
            return wsResult;
        } catch (Throwable t) {
            if (exceptionMapping != null) {
                Throwable mappedException = exceptionMapping.d2w(t);
                throw mappedException;
            }
            throw t;
        }
    }

    protected Object[] mapArgs(Object[] args, Method m, Method method) {
        if (args != null) {
            Object[] mappedArgs = new Object[args.length];
            Type[] ts = m.getGenericParameterTypes();
            for (int i = 0; i < args.length; i++) {
                mappedArgs[i] = map.w2d(args[i], ts[i]);
            }
            return mappedArgs;
        } else {
            return null;
        }
    }
}
