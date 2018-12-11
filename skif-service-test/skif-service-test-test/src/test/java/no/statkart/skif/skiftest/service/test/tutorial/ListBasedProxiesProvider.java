package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.List;

public class ListBasedProxiesProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final ProxyHandler<S> proxyHandler;

    @Inject
    public ListBasedProxiesProvider(TypeLiteral<S> type,
                                    ToImplementationProxyHandler<S> implementationProxyHandler,
                                    List<ChainedProxyHandler<S>> proxyHandlerList) {
        this.type = type;
        ProxyHandler<S> prev = implementationProxyHandler;
        for (ChainedProxyHandler<S> next: proxyHandlerList ) {
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
