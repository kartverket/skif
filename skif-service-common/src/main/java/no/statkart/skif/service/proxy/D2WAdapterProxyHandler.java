package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

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
 * @since 1.1
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

    @Override
    protected Method findMethod(Method method) throws NoSuchMethodException {
        for (Method m : adapteeClass.getMethods()) {
            if (m.getName().equals(method.getName())) {
                return m;
            }
        }
        throw new ImplementationException("Manglende metode i adaptor: " + method.getName());
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Method m = getMethod(method);

        Object[] mappedArgs = mapArgs(args, m);

        try {
            Object result = adapteeRoot.invoke(proxy, m, mappedArgs);
            return map.w2d(result, method.getReturnType());

        } catch (Throwable t) {
            if (exceptionMapping != null) {
                Throwable mappedException = exceptionMapping.w2d(t);
                throw mappedException;
            } else {
                throw t;
            }

        }
    }

    protected Object[] mapArgs(Object[] args, Method m) {
        Object[] mappedArgs = map.d2w(args, m.getParameterTypes());
        return mappedArgs;
    }
}
