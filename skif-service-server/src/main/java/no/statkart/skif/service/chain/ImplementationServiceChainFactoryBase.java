package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.InvokeViaProviderProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import javax.annotation.Nullable;

/**
 * Denne factory sette opp default {@code ImplementationServiceChain} for service av type {@code S}.
 * Kjeden bruker en {@link InvokeViaProviderProxyHandler} slik at opprettelsen av en instans av type {@code S}
 * ikke skjer i det kjeden settes opp, men først når et kall blir utført igjennom kjeden. Dette gjøres for å
 * gi kjedeledd mulighet for å sette opp ressurshåndtering og andre tjenester som servicen instansen trenger.
 *
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

    /**
     * Oppretter kjedeledd. Kan kalles uten et aktiv {@link no.statkart.skif.service.scope.ServiceRequestScope}
     */
    @Override
    public ProxyHandler<S> createChain() {
        return new InvokeViaProviderProxyHandler<S>(implementationProvider);
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new UnsupportedOperationException();
    }
}
