package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.SingleVmRemoteCallProxyHandler;

import javax.annotation.Nullable;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ClientCallServiceChainFactorySingleVm<S> implements CallServiceChainFactory<S> {
    protected final Provider<SingleVmRemoteCallProxyHandler<S>> singleVmRemoteCallProxyHandlerProvider;

    @Inject
    public ClientCallServiceChainFactorySingleVm(Provider<SingleVmRemoteCallProxyHandler<S>> singleVmRemoteCallProxyHandlerProvider) {
        this.singleVmRemoteCallProxyHandlerProvider = singleVmRemoteCallProxyHandlerProvider;
    }


    @Override
    public float getChainPosition() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProxyHandler<S> createChain() {
        return singleVmRemoteCallProxyHandlerProvider.get();
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new UnsupportedOperationException();
    }
}
