package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.service.logging.ServerLoggingProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import javax.annotation.Nullable;

public class WSServiceChainFactoryWithLogging<S> extends WSServiceChainFactoryBase<S> {
    private final Provider<ServerLoggingProxyHandler<S>> serverLoggingProxyHandlerProvider;

    @Inject
    public WSServiceChainFactoryWithLogging(TypeLiteral<S> type, Provider<ServerLoggingProxyHandler<S>> serverLoggingProxyHandlerProvider, @WSServiceChain TerminatingProxyHandler<S> proxyHandler) {
        super(type, proxyHandler);
        this.serverLoggingProxyHandlerProvider = serverLoggingProxyHandlerProvider;
    }

    @Override
    public float getChainPosition() {
        return 9;
    }

    @Override
    public ProxyHandler<S> createChain() {
        ServerLoggingProxyHandler<S> serverLoggingProxyHandler = serverLoggingProxyHandlerProvider.get();
        serverLoggingProxyHandler.setChained(super.createChain());
        return serverLoggingProxyHandler;
    }

}
