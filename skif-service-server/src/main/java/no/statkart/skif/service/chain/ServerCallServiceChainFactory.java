package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import jakarta.annotation.Nullable;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.annotation.Call;
import no.statkart.skif.service.ejb.EJBAttributesLookup;
import no.statkart.skif.service.ejb.EJBCallTypeChooserProxyHandler;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.List;

/**
 * Denne factory setter opp {@code CallServiceChain} på serveren. {@code SINGLE_VM}- og {@code JEE}-mode bruker
 * samme {@code ServiceCallChain}.
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerCallServiceChainFactory<S> implements CallServiceChainFactory<S> {
    private TypeLiteral<S> type;
    private final ImplementationServiceChainFactory<S> implementationServiceChainFactory;
    private final Provider<EJBCallTypeChooserProxyHandler<S>> ejbInvokerProxyHandlerProvider;
    private final EJBAttributesLookup<S> ejbAttributesLookup;
    private final Provider<List<ChainedProxyHandler<S>>> callProxyHandlerListProvider;

    @Inject
    public ServerCallServiceChainFactory(TypeLiteral<S> type, ImplementationServiceChainFactory<S> implementationServiceChainFactory, Provider<EJBCallTypeChooserProxyHandler<S>> ejbInvokerProxyHandlerProvider, EJBAttributesLookup<S> ejbAttributesLookup, @Call Provider<List<ChainedProxyHandler<S>>> callProxyHandlerListProvider) {
        this.type = type;
        this.implementationServiceChainFactory = implementationServiceChainFactory;
        this.ejbInvokerProxyHandlerProvider = ejbInvokerProxyHandlerProvider;
        this.ejbAttributesLookup = ejbAttributesLookup;
        this.callProxyHandlerListProvider = callProxyHandlerListProvider;
    }

    @Override
    public float getChainPosition() {
        return 0;
    }

    @Override
    public ProxyHandler<S> createChain() {
        ProxyHandler<S> head;
        if (ejbAttributesLookup.isEjbCallsNotRequired()) {
            head = implementationServiceChainFactory.createChain();
        } else {
            head = ejbInvokerProxyHandlerProvider.get();
        }
        final List<ChainedProxyHandler<S>> proxyHandlerList = callProxyHandlerListProvider.get();
        for (int i = proxyHandlerList.size() - 1; i >= 0; i--) {
            head = proxyHandlerList.get(i).setChained(head);
        }
        return head;
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new NotImplementedException();
    }
}
