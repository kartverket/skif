package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Deprecated
public class EJBServiceChainFactoryWithTx<S> implements EJBServiceChainFactory<S> {
    private final Provider<EJBResourceProxyHandler<S>> ejbResourceProxyHandlerProvider;

    @Inject
    public EJBServiceChainFactoryWithTx(Provider<EJBResourceProxyHandler<S>> ejbResourceProxyHandlerProvider) {
        this.ejbResourceProxyHandlerProvider = ejbResourceProxyHandlerProvider;
    }

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
        EJBResourceProxyHandler ejbResourceProxyHandler = ejbResourceProxyHandlerProvider.get();
        ejbResourceProxyHandler.setChained(firstInChain);
        return ejbResourceProxyHandler;
    }
}
