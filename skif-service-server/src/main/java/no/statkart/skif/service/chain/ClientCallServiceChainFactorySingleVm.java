package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.service.annotation.Call;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.SingleVmRemoteCallProxyHandler;

import jakarta.annotation.Nullable;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ClientCallServiceChainFactorySingleVm<S> implements CallServiceChainFactory<S> {
    protected final Provider<SingleVmRemoteCallProxyHandler<S>> singleVmRemoteCallProxyHandlerProvider;
    private final Provider<List<ChainedProxyHandler<S>>> callProxyHandlerListProvider;

    @Inject
    public ClientCallServiceChainFactorySingleVm(Provider<SingleVmRemoteCallProxyHandler<S>> singleVmRemoteCallProxyHandlerProvider, @Call Provider<List<ChainedProxyHandler<S>>> callProxyHandlerListProvider) {
        this.singleVmRemoteCallProxyHandlerProvider = singleVmRemoteCallProxyHandlerProvider;
        this.callProxyHandlerListProvider = callProxyHandlerListProvider;
    }


    @Override
    public float getChainPosition() {
        return 0;
    }

    @Override
    public ProxyHandler<S> createChain() {
        ProxyHandler<S> head;
        try {
            head = singleVmRemoteCallProxyHandlerProvider.get();
        } catch (ProvisionException e) {
            throw new ConfigurationException("Service not bound in ServerModule", e);
        }
        final List<ChainedProxyHandler<S>> proxyHandlerList = callProxyHandlerListProvider.get();
        for (int i = proxyHandlerList.size() - 1; i >= 0; i--) {
            head = proxyHandlerList.get(i).setChained(head);
        }
        return head;
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new UnsupportedOperationException();
    }
}
