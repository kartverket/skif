package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.Set;

public class MultibinderBasedProxiesProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final ProxyHandler<S> proxyHandler;


    @Inject
    public MultibinderBasedProxiesProvider(TypeLiteral<S> type,
                                           ToImplementationProxyHandler<S> implementationProxyHandler,
                                           Set<ChainedProxyHandler<S>> proxyHandlerSet) {
        this.type = type;
        ProxyHandler<S> prev = implementationProxyHandler;
        for (ChainedProxyHandler<S> next: proxyHandlerSet ) {  // Problematisk at det er et Set. Kan ikke bestemme rekkefølgen
            prev = next.setChained(prev);
        }
        this.proxyHandler = prev;
    }


    @Override
    public S get() {
        S instanceOrProxy = proxyHandler.buildProxy(type);
        return instanceOrProxy;
    }
}
