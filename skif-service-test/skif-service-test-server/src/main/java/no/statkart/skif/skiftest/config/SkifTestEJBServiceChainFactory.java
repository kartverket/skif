package no.statkart.skif.skiftest.config;

import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.RuntimeExceptionProxyHandler;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class SkifTestEJBServiceChainFactory<S> implements EJBServiceChainFactory<S> {

    @Override
    public float getChainPosition() {
        return 10;
    }

    @Override
    public ProxyHandler<S> createChain() {
        throw new NotImplementedException();
    }

    @Override
    public ProxyHandler<S> extendChain(ProxyHandler<S> firstInChain) {
        final RuntimeExceptionProxyHandler handler = new RuntimeExceptionProxyHandler();
        handler.setChained(firstInChain);
        return handler;
    }
}
