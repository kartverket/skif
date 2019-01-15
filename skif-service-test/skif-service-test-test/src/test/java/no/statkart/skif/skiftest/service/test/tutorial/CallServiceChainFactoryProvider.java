package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import javax.annotation.Nullable;

public class CallServiceChainFactoryProvider<S> implements Provider<CallServiceChainFactory<S>> {
    private final TypeLiteral<? extends ProxyHandler<S>> proxyHandlerType;
    private final float order;
    @Inject
    private Injector injector;

    @Inject
    public CallServiceChainFactoryProvider(TypeLiteral<? extends ProxyHandler<S>>  proxyHandlerType, float order) {
        this.proxyHandlerType = proxyHandlerType;
        this.order = order;
    }

    @Override
    public CallServiceChainFactory<S> get() {
        final ProxyHandler<S> proxyHandler = injector.getInstance(Key.get(proxyHandlerType));
        return new CallServiceChainFactory<S>() {
            @Override
            public float getChainPosition() {
                return order;
            }

            @Override
            public ProxyHandler<S> createChain() {
                return proxyHandler;
            }

            @Override
            public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
                return ((ChainedProxyHandler<S>)proxyHandler).setChained(firstInChain);
            }
        };
    }
}
