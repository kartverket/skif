package no.statkart.skif.service.chain;

import com.google.inject.*;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.service.ejb.EJBAttributesLookup;
import no.statkart.skif.service.ejb.EJBCallTypeChooserProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import javax.annotation.Nullable;

/**
 * Denne factory setter opp {@code CallServiceChain} på serveren. {@code SINGLE_VM}- og {@code JEE}-mode bruker
 * samme {@code ServiceCallChain}.
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ServerCallServiceChainFactory<S> implements CallServiceChainFactory<S> {
    private TypeLiteral<S> type;
    private final ImplementationServiceChainFactory<S> implementationServiceChainFactory;
    private final Provider<EJBCallTypeChooserProxyHandler<S>> ejbInvokerProxyHandlerProvider;
    private final EJBAttributesLookup<S> ejbAttributesLookup;

    @Inject
    public ServerCallServiceChainFactory(TypeLiteral<S> type, ImplementationServiceChainFactory<S> implementationServiceChainFactory, Provider<EJBCallTypeChooserProxyHandler<S>> ejbInvokerProxyHandlerProvider, EJBAttributesLookup<S> ejbAttributesLookup) {
        this.type = type;
        this.implementationServiceChainFactory = implementationServiceChainFactory;
        this.ejbInvokerProxyHandlerProvider = ejbInvokerProxyHandlerProvider;
        this.ejbAttributesLookup = ejbAttributesLookup;
    }

    @Override
    public float getChainPosition() {
        return 0;
    }

    @Override
    public ProxyHandler<S> createChain() {
        if (ejbAttributesLookup.isEjbCallsNotRequired()) {
            return implementationServiceChainFactory.createChain();
        } else {
            return ejbInvokerProxyHandlerProvider.get();
        }
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new ConfigurationException();
    }
}
