package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import jakarta.annotation.Nullable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class WSServiceChainFactoryBase<S> implements WSServiceChainFactory<S> {
    protected final TypeLiteral<S> type;
    protected final TerminatingProxyHandler<S> proxyHandler;

    @Inject
    public WSServiceChainFactoryBase(TypeLiteral<S> type, @WSServiceChain TerminatingProxyHandler<S> proxyHandler) {
        this.type = type;
        this.proxyHandler = proxyHandler;
    }

    @Override
    public float getChainPosition() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProxyHandler<S> createChain() {
        return proxyHandler;
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new NotImplementedException();
    }
}
