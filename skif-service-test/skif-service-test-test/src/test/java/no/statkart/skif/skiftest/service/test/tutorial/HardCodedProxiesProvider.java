package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ProxyHandler;

public class HardCodedProxiesProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final ProxyHandler<S> proxyHandler;

    @Inject
    public HardCodedProxiesProvider(TypeLiteral<S> type,
                                    ToImplementationProxyHandler<S> implementationProxyHandler,
                                    CallCountingProxyHandler<S> callCountingProxyHandler,
                                    ExceptionCountingProxyHandler<S> exceptionCountingProxyHandler) {
        this.type = type;
        ProxyHandler<S> prev = implementationProxyHandler;
        prev = callCountingProxyHandler.setChained(prev);
        prev = exceptionCountingProxyHandler.setChained(prev);
        this.proxyHandler = prev;
    }


    @Override
    public S get() {
        S instanceOrProxy = proxyHandler.buildProxy(type);
        return instanceOrProxy;
    }
}
