package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.ws.WebServiceExceptionMapper;
import no.statkart.skif.service.ws.JaxWsServiceProvider;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Kobler umappede webservice-kall opp mot JAX-WS-kjeden.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class RemoteWSServiceModuleStrategyJEE extends RemoteWSServiceModuleStrategy implements JaxWsModule.JaxWsModuleStrategyJEE {

    private final JaxWsServiceProvider.PoolConfig.Builder poolConfigBuilder;
    private final List<Class<? extends Handler>> jaxwsHandlerClasses;
    private Function<? super String, String> portTypeToServiceNameStrategy;
    private Function<? super QName, String> serviceNameToWsdlLocationStrategy;

    public RemoteWSServiceModuleStrategyJEE() {
        poolConfigBuilder = JaxWsServiceProvider.PoolConfig.builder();
        jaxwsHandlerClasses = new ArrayList<>(JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_JAXWS_HANDLER_CLASSES);
        portTypeToServiceNameStrategy = JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_PORT_TYPE_TO_SERVICE_NAME_STRATEGY;
        serviceNameToWsdlLocationStrategy = JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_SERVICE_NAME_TO_WSDL_LOCATION_STRATEGY;
    }

    @Override
    public void requireBindings(Binder binder) {
        binder.install(JaxWsModule.Common.getInstance());
    }

    @Override
    public <S> void bindCallServiceChainFactoryForService(Binder outerBinder, Class<S> service) {
    }

    @Override
    public <S> void bindService(Binder outerBinder, Class<S> service) {
        List<Provider<? extends Handler>> handlerProviders = jaxwsHandlerClasses.stream().map(outerBinder::getProvider).collect(Collectors.toList());
        TypeLiteral<Optional<HostnameVerifier>> optionalHostnameVerifierType = SkifUtil.typeLiteral(Optional.class, HostnameVerifier.class);
        JaxWsServiceProvider<S> jaxWsServiceProvider = new JaxWsServiceProvider<>(
                poolConfigBuilder.build(),
                service,
                portTypeToServiceNameStrategy,
                serviceNameToWsdlLocationStrategy,
                handlerProviders,
                outerBinder.getProvider(WebServiceExceptionMapper.class),
                outerBinder.getProvider(ServerUrlHolder.class),
                outerBinder.getProvider(Key.get(optionalHostnameVerifierType)));
        outerBinder.bind(service).toProvider(jaxWsServiceProvider).in(Singleton.class);
    }

    @Override
    public void setMaxIdlePooled(int maxIdlePooled) {
        poolConfigBuilder.withMaxIdle(maxIdlePooled);
    }

    @Override
    public void setMaxTotalPooled(int maxTotalPooled) {
        poolConfigBuilder.withMaxTotal(maxTotalPooled);
    }

    @Override
    public void setMinIdlePooled(int minIdlePooled) {
        poolConfigBuilder.withMinIdle(minIdlePooled);
    }

    @Override
    public void setPortTypeToServiceNameStrategy(Function<? super String, String> portTypeToServiceNameStrategy) {
        this.portTypeToServiceNameStrategy = Objects.requireNonNull(portTypeToServiceNameStrategy, "portTypeToServiceNameStrategy");
    }

    @Override
    public void setServiceNameToWsdlLocationStrategy(Function<? super QName, String> serviceNameToWsdlLocationStrategy) {
        this.serviceNameToWsdlLocationStrategy = Objects.requireNonNull(serviceNameToWsdlLocationStrategy, "serviceNameToWsdlLocationStrategy");
    }

    @Override
    public List<Class<? extends Handler>> getJaxwsHandlerClasses() {
        return jaxwsHandlerClasses;
    }
}
