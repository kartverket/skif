package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EJBServiceChainFactoryImpl<S> implements EJBServiceChainFactory<S> {
    private final Provider<List<ChainedProxyHandler<S>>> ejbProxyHandlerListProvider;

    @Inject
    public EJBServiceChainFactoryImpl(Provider<List<ChainedProxyHandler<S>>> ejbProxyHandlerListProvider) {
        this.ejbProxyHandlerListProvider = ejbProxyHandlerListProvider;
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
        ProxyHandler<S> head = firstInChain;
        final List<ChainedProxyHandler<S>> proxyHandlerList = ejbProxyHandlerListProvider.get();
        for (int i = proxyHandlerList.size()-1; i >=0; i--) {
            head = proxyHandlerList.get(i).setChained(head);
        }
        return head;
    }
}
