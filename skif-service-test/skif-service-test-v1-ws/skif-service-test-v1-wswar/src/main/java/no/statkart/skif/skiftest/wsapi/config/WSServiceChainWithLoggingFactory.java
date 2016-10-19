package no.statkart.skif.skiftest.wsapi.config;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.service.chain.WSServiceChainFactoryBase;
import no.statkart.skif.service.logging.ServerCallLogger;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.proxy.WsLoggingProxyHandler;

public class WSServiceChainWithLoggingFactory<S> extends WSServiceChainFactoryBase<S> {
    private final ServerCallLogger serviceCallLogger;

    @Inject
    public WSServiceChainWithLoggingFactory(TypeLiteral<S> type, @WSServiceChain TerminatingProxyHandler<S> proxyHandler, ServerCallLogger serviceCallLogger) {
        super(type, proxyHandler);
        this.serviceCallLogger = serviceCallLogger;
    }

    @Override
    public ProxyHandler<S> createChain() {
        return new WsLoggingProxyHandler<S>(serviceCallLogger).setChained(super.createChain());
    }
}