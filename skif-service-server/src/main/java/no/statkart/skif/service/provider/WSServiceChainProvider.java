package no.statkart.skif.service.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.chain.ImplementationServiceChainFactory;
import no.statkart.skif.service.chain.WSServiceChainFactory;
import no.statkart.skif.service.proxy.ProxyHandler;

/**
 * Guice provider for å lage en {@code WSServiceChain} instans av type {@code T}. For å unngå å komme i konflikt
 * med Guice binder for {@code T} bør denne provider bindes til {@code T} med annotation {@code @WSServiceChain}.
 * <p/>
 * Klassen bruker {@code WSServiceChainFactory<T>} til å lage en kjede av {@code ProxyHandler<T>}'ere og
 * legger en {@code Proxy<T>} foran.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public class WSServiceChainProvider<T> implements Provider<T> {
    final private TypeLiteral<T> type;
    final private WSServiceChainFactory<T> wsServiceChainFactory;

    @Inject
    public WSServiceChainProvider(TypeLiteral<T> type, WSServiceChainFactory<T> wsServiceChainFactory) {
        this.type = type;
        this.wsServiceChainFactory = wsServiceChainFactory;
    }

    @Override
    public T get() {
        T proxy = wsServiceChainFactory.createChain().buildProxy(type);
        return proxy;
    }
}
