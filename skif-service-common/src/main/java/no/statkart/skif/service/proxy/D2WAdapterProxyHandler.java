package no.statkart.skif.service.proxy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.SnapshotVersion;

import javax.annotation.Nullable;
import javax.xml.ws.WebFault;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Adapter proxy som adapterer domain interface T til webservice interface A ved å mappe metoder med samme navn til hverandre og transformere
 * argumentene og resultatet vha et mapping2 objekt
 * <p>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Alle {@link Exception}s annotert med {@link WebFault} blir mappet over til korresponderende exceptions ihht til mapper. All andre exceptions blir fanget og wrappet til
 * {@link ImplementationException}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class D2WAdapterProxyHandler<T, A> extends AdapterProxyHandler<T, A> {

    final protected Mapping map;

    /**
     * Optional mapping2 for exceptions. Can be <tt>null</tt> if not set.
     */
    @Inject(optional = true)
    @Nullable
    final protected ExceptionMapping exceptionMapping;


    @Inject()
    public D2WAdapterProxyHandler(Provider<A> adapteeProvider, TypeLiteral<A> aType, Mapping map) {
        this(adapteeProvider, aType, map, null);
    }

    public D2WAdapterProxyHandler(Provider<A> adapteeProvider, TypeLiteral<A> aType, Mapping map, ExceptionMapping exceptionMapping) {
        super(adapteeProvider, (Class<A>) aType.getRawType());
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
        int length = args == null ? 0 : args.length;
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
                //forventer kun exceptions definert for webservice api. Disse er da annotert med @WebFault
                if (t.getClass().getAnnotation(WebFault.class) != null) {
                    Throwable mappedException = exceptionMapping.w2d(t);
                    throw mappedException;
                }
            }
            throw t;
        }
    }

    protected Object mapResult(Method method, Method m, Object result) {
        TypeToken<?> fromTypeToken = TypeToken.of(adapteeClass).resolveType(m.getGenericReturnType());
        TypeToken<?> toTypeToken = TypeToken.of(method.getDeclaringClass()).resolveType(method.getGenericReturnType());
        return map.w2d(result, fromTypeToken.getType(), toTypeToken.getType());
    }

    /**
     * Mapper argumenter i args slik at de kan brukes som innput parametre til  metode {@code toMethod}.
     * Det opprettes like mange parametre som det {@code toMethod} krever, men det mappes kun {@code length}
     * antall argumenter fra {@code args}. Metoden er laget slik fordi {@code toMethod} kan ha en ekstra context
     * parameter som det må settes plass av til og videre så kan {@code fromMethod} kan ha en {@code SnapshotVersion}
     * parameter som skal mappes via context parameteren og derfor ikke skal mappes her.
     */
    protected Object[] mapArgs(Object[] args, Method fromMethod, Method toMethod, int length) {
        Object[] mappedArgs;
        if (args != null) {
            Type[] toTypes = toMethod.getGenericParameterTypes();
            mappedArgs = new Object[toTypes.length];
            Type[] fromTypes = fromMethod.getGenericParameterTypes();
            for (int i = 0; i < length; i++) {
                mappedArgs[i] = map.d2w(args[i], fromTypes[i], toTypes[i]);
            }
            return mappedArgs;
        } else {
            mappedArgs = new Object[toMethod.getParameterTypes().length];
        }
        return mappedArgs;
    }
}
