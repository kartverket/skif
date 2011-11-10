package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.SingleVmRemoteCallProxyHandler;

import javax.annotation.Nullable;

/**
 * @author Henrik Fredholm
 * @since 2.0
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
        try {
            return singleVmRemoteCallProxyHandlerProvider.get();
        } catch (ProvisionException e) {
            throw new ConfigurationException("Service not bound in ServerModule", e);
        }
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new UnsupportedOperationException();
    }
}
