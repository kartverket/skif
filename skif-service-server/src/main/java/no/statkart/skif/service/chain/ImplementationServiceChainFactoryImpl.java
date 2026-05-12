package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.annotation.Nullable;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.InvokeViaProviderProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.List;

/**
 * Denne factory sette opp default {@code ImplementationServiceChain} for service av type {@code S}.
 * Kjeden bruker en {@link InvokeViaProviderProxyHandler} slik at opprettelsen av en instans av type {@code S}
 * ikke skjer i det kjeden settes opp, men først når et kall blir utført igjennom kjeden. Dette gjøres for å
 * gi kjedeledd mulighet for å sette opp ressurshåndtering og andre tjenester som servicen instansen trenger.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ImplementationServiceChainFactoryImpl<S> implements ImplementationServiceChainFactory<S> {
    private final Provider<S> implementationProvider;
    private final Provider<List<ChainedProxyHandler<S>>> implementationProxyHandlerListProvider;

    @Inject
    public ImplementationServiceChainFactoryImpl(@Implementation Provider<S> implementationProvider, @Implementation Provider<List<ChainedProxyHandler<S>>> implementationProxyHandlerListProvider) {
        this.implementationProvider = implementationProvider;
        this.implementationProxyHandlerListProvider = implementationProxyHandlerListProvider;
    }

    @Override
    public float getChainPosition() {
        return 0;
    }

    /**
     * Oppretter kjedeledd. Kan kalles uten et aktiv {@link no.statkart.skif.service.scope.ServiceRequestScope}
     */
    @Override
    public ProxyHandler<S> createChain() {
        ProxyHandler<S> head = new InvokeViaProviderProxyHandler<S>(implementationProvider);
        final List<ChainedProxyHandler<S>> proxyHandlerList = implementationProxyHandlerListProvider.get();
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
