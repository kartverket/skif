package no.statkart.skif.service.proxy;

import com.google.inject.Provider;

import java.lang.reflect.Method;

/**
 * En {@code TerminatingProxyHandler} som tar en provider for en annen {@code TerminatingProxyHandler} som argument. Kall
 * på provideren skjer først ved invoke slik at opprettelsen utsettes til rett før den blir kalt i setdet for å bli
 * opprettet ifm konstruksjon av proxykjeden.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class TerminatingProviderProxyHandler<S> extends TerminatingProxyHandler<S> {
    protected Provider<TerminatingProxyHandler<S>> provider;

    protected TerminatingProviderProxyHandler(Provider<TerminatingProxyHandler<S>> provider) {
        this.provider = provider;
    }

    @Override
    protected final Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        TerminatingProxyHandler<S> proxyHandler = provider.get();
        return proxyHandler.invoke(proxy, method, args);
    }
}
