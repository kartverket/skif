package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;

/**
 * FactorySpecifikasjon for {@link EJBServiceChainFactoryWithTx}
 * </p>
 * Denne factory anvender følgende proxyies per service:
 * <ul>
 *     <li>{@code EJBResourceProxyHandler<S>}</li>
 * </ul>
 * Denne factory forventer at følge typer allerede er definert:
 * <ul>
 *     <li>{@code EJBResourceManager}</li>
 * </ul>
 *
 * @author HenriNk Fredholm
 */
@Deprecated
public class EJBServiceChainFactoryWithTxSpecification extends EJBServiceChainFactorySpecification {

    public EJBServiceChainFactoryWithTxSpecification(Class<? extends EJBResourceProxyHandler> ejbResourceProxyHandlerImplentationClass) {
        this(EJBServiceChainFactoryImpl.class, ejbResourceProxyHandlerImplentationClass);
    }

    public EJBServiceChainFactoryWithTxSpecification(Class<? extends EJBServiceChainFactory> factoryClass, Class<? extends EJBResourceProxyHandler> ejbResourceProxyHandlerImplentationClass) {
        super(factoryClass, ejbResourceProxyHandlerImplentationClass);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        super.bindProxyHandlersForService(binder, service);
        requireBinding(binder, ConnectionManager.class);
    }
}
