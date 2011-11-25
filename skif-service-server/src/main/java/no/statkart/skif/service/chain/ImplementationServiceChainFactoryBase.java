package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.InstanceCallProxyHandler;
import no.statkart.skif.service.proxy.ProviderCallProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProviderProxyHandler;

import javax.annotation.Nullable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ImplementationServiceChainFactoryBase<S> implements ImplementationServiceChainFactory<S> {
    protected final Provider<S> implementationProvider;

    @Inject
    public ImplementationServiceChainFactoryBase(@Implementation Provider<S> implementationProvider) {
        this.implementationProvider = implementationProvider;
    }

    @Override
    public float getChainPosition() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProxyHandler<S> createChain() {
//        final S instance = implementationProvider.get();
//        return new InstanceCallProxyHandler<S>(instance);
        return new ProviderCallProxyHandler<S>(implementationProvider);
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new UnsupportedOperationException();
    }
}
