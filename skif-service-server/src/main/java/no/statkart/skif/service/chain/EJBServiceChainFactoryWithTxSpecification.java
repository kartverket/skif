package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.ejb.EJBResourceManager;
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
 * @author Henrik Fredholm
 */
public class EJBServiceChainFactoryWithTxSpecification extends EJBServiceChainFactorySpecification {
    public EJBServiceChainFactoryWithTxSpecification() {
        super(EJBServiceChainFactoryWithTx.class);
    }

    public EJBServiceChainFactoryWithTxSpecification(Class<? extends EJBServiceChainFactory> factoryClass) {
        super(factoryClass);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        super.bindProxyHandlersForService(binder, service);
        TypeLiteral<EJBResourceProxyHandler<S>> ejbResourceProxyHandlerType = SkifUtil.typeLiteral(EJBResourceProxyHandler.class, service);
        binder.bind(ejbResourceProxyHandlerType);
        requireBinding(binder, EJBResourceManager.class);
    }

    /**
     * Oppretter en avhengighet fra modulen definert av {@code binder} til {@code type}.
     * Når injectoren opprettes så vil Guide rapportere en feil hvis {@code type} ikke
     * kan injectes.
     *
     * @since 2.0
     */
    protected void requireBinding(Binder binder, Class<?> type) {
        binder.getProvider(type);
    }

}
