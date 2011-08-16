package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class EJBServiceChainFactoryBase<S> implements EJBServiceChainFactory<S> {
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
        return firstInChain;
    }
}
