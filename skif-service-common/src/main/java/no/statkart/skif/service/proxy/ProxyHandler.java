package no.statkart.skif.service.proxy;

import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Abstrakt superklasse for {@link ProxyHandler} som kan inngå i en kjede av {@code ProxyHandler}s, også kalt
 * en {@code ServiceChain}. Formålet med en {@code ServiceChain} er å utfører operasjoner i forkant eller etterkant
 * av servicekallet.
 *
 * <p>
 *    I en {@code ServiceChain} vil alle {@code ProxyHandler}-ledd på nær det siste normalt være av subtypen
 *    {@link ChainedProxyHandler} og siste ledd vil normalt vil være av subtypen {@link TerminatingProxyHandler}.
 *    Dette er dog ikke noe formelt krav. En {@code ServiceChain} representeres ikke av en egen klasse men ved den
 *    første {@code ProxyHandler} i kjeden. Det er mulig å utvide en {@code ServiceChain} ved å legge på flere
 *    {@code ProxyHandlere} foran en eksisterende {@code ServiceChain}, men det er ingen funksjonalitet for å
 *    legge på {@code ProxyHandler} bak en eksisterende {@code ServiceChain}.
 * </p>
 * Klassen implementere en hjelpemetode {@link #buildProxy(Class)} for å lage en proxy som implementerer
 * interface {@code <S>} for første ledd i kjeden.
 *
 * @author Henrik Fredholm
 */
public abstract class ProxyHandler<S> implements InvocationHandler {
    public S buildProxy(Class<S> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, this));
    }

    public final S buildProxy(TypeLiteral<S> type) {
        return (S) buildProxy((Class<S>)type.getRawType());
    }

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isBuildInMethod(method)) return invokeBuildInMethod(method, args);
        return invokeMethod(proxy, method, args);
    }

    protected abstract Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable;

    private boolean isBuildInMethod(Method method) {
        final String name = method.getName();
        return name.equals("toString") || name.equals("hashCode") || name.equals("equals");
    }

    private Object invokeBuildInMethod(Method method, Object[] args) throws Throwable {
        try {
            //noinspection UnnecessaryLocalVariable
            final Object result = method.invoke(this, args);
            return result;
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}