package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.service.logging.ServerLoggingProxyHandler;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

/**
 * FactorySpecifikasjon for {@link ServerLoggingProxyHandler}
 * </p>
 * Denne factory anvender følgende proxyies per service:
 * <ul>
 *     <li>{@code ServerLoggingProxyHandler<S>}</li>
 * </ul>
 * Denne factory forventer at følge typer allerede er definert:
 * <ul>
 *     <li>{@code EJBResourceManager}</li>
 * </ul>
 *
 * @author Jan Holmen
 */
public class WSServiceChainFactoryWithLoggingSpecification extends WSServiceChainFactorySpecification {
    private final Class<? extends ChainedProxyHandler> serverLoggingProxyHandleImplementationClass;

    public WSServiceChainFactoryWithLoggingSpecification(Class<? extends ChainedProxyHandler> serverLoggingProxyHandleImplementationClass) {
        this(WSServiceChainFactoryWithLogging.class,serverLoggingProxyHandleImplementationClass);
    }


    public WSServiceChainFactoryWithLoggingSpecification(Class<? extends WSServiceChainFactoryBase> factoryClass, Class<? extends ChainedProxyHandler> serverLoggingProxyHandleImplementationClass) {
        super(factoryClass);
        this.serverLoggingProxyHandleImplementationClass = serverLoggingProxyHandleImplementationClass;
    }


    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        super.bindProxyHandlersForService(binder, service);

        TypeLiteral<ServerLoggingProxyHandler<S>> serverLoggingProxyHandlerType = SkifUtil.typeLiteral(ChainedProxyHandler.class, service);
        TypeLiteral<? extends ServerLoggingProxyHandler<S>>  serverLoggingProxyHandlerImplType = SkifUtil.typeLiteral(serverLoggingProxyHandleImplementationClass, service);

        binder.bind(serverLoggingProxyHandlerType).to(serverLoggingProxyHandlerImplType);
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
