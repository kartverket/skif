package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.annotation.WSServiceChain;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Adapter proxy som adapterer domain interface T til webservice interface A ved å mappe metoder med samme navn til hverandre og transformere
 * argumentene og resultatet vha et mapping2 objekt
 * <p/>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Mapperen får som rolle å holde styr på evt wrapping av exceptions. Et eksempel kan være å wrappe alle ikke skif exceptions i en {@link no.statkart.skif.exception.ImplementationException}.
 *
 * @author Henrik Fredholm
 * @NotTheadSafe
 * @since 1.1
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

    @Override
    protected Method findMethod(Method method) throws NoSuchMethodException {
        for (Method m : adapteeClass.getMethods()) {
            if (m.getName().equals(method.getName()))  {
                return m;
            }
        }
        throw new ImplementationException("Manglende metode i adaptor: " + method.getName());
    }


    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Method adapteeMethod = getMethod(method);
        Object[] mappedArgs;

        try {
            mappedArgs = mapArgs(args, adapteeMethod);
            Object result = adapteeRoot.invoke(proxy, adapteeMethod,  mappedArgs);
            return map.d2w(result, method.getReturnType());
        } catch (Throwable t) {
            if (exceptionMapping != null) {
                Throwable mappedException = exceptionMapping.d2w((Throwable)t);
                throw mappedException;
            }
            throw t;
        }
    }

    protected Object[] mapArgs(Object[] args, Method m) {
        Object[] mappedArgs = map.w2d(args, m.getParameterTypes());
        return mappedArgs;
    }
}
