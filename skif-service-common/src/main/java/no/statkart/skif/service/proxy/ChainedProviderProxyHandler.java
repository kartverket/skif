package no.statkart.skif.service.proxy;

import com.google.inject.Provider;

import java.lang.reflect.Method;

/**
 * En {@code ChainedProxyHandler} som tar en provider for en annen {@code ChainedProxyHandler} som argument. Kall
 * på provideren skjer først ved invoke slik at opprettelsen utsettes til rett før den blir kalt i setdet for å bli
 * opprettet ifm konstruksjon av proxykjeden.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class ChainedProviderProxyHandler<S> extends ChainedProxyHandler<S> {
    protected Provider<ChainedProxyHandler<S>> provider;
    protected Class<S> type;

    protected ChainedProviderProxyHandler(Provider<ChainedProxyHandler<S>> provider) {
        this.provider = provider;
    }

    public ChainedProviderProxyHandler setType(Class<S> type) {
        this.type = type;
        return this;
    }

    @Override
    protected final Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        ChainedProxyHandler<S> proxyHandler = provider.get();
        proxyHandler.setChained(proxyHandler);
        return proxyHandler.invoke(proxy, method, args);
    }
}

