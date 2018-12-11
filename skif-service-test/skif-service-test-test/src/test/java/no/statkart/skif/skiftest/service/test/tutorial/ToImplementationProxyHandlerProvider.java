package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public class ToImplementationProxyHandlerProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final ToImplementationProxyHandler<S> proxyHandler;

    @Inject
    public ToImplementationProxyHandlerProvider(TypeLiteral<S> type, ToImplementationProxyHandler<S> proxyHandler) {
        this.type = type;
        this.proxyHandler = proxyHandler;
    }


    @Override
    public S get() {
        S instanceOrProxy = proxyHandler.buildProxy(type);
        return instanceOrProxy;
    }
}
