package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.ws.WebServiceExceptionMapper;
import no.statkart.skif.service.ws.JaxWsServiceProvider;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.xml.namespace.QName;
import jakarta.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class WSRemoteServiceModule extends SkifModule implements JaxWsModule.JaxWsModuleStrategyJEE {
    protected List<Class<?>> serviceClasses;
    private final JaxWsServiceProvider.PoolConfig.Builder poolConfigBuilder;
    private final List<Class<? extends Handler>> jaxwsHandlerClasses;
    private Function<? super String, String> portTypeToServiceNameStrategy;
    private Function<? super QName, String> serviceNameToWsdlLocationStrategy;

    public WSRemoteServiceModule(ModuleConfiguration moduleConfiguration, List<Class<?>> serviceClasses) {
        super(moduleConfiguration);
        this.serviceClasses = serviceClasses;
        poolConfigBuilder = JaxWsServiceProvider.PoolConfig.builder();
        jaxwsHandlerClasses = new ArrayList<>(JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_JAXWS_HANDLER_CLASSES);
        portTypeToServiceNameStrategy = JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_PORT_TYPE_TO_SERVICE_NAME_STRATEGY;
        serviceNameToWsdlLocationStrategy = JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_SERVICE_NAME_TO_WSDL_LOCATION_STRATEGY;
    }

    @Override
    protected void configure() {
        install(JaxWsModule.Common.getInstance());
        for (Class<?> serviceClass : serviceClasses) {
            bindJaxWsServiceProvider(binder(), serviceClass);
        }
    }

    private <T> void bindJaxWsServiceProvider(Binder binder, Class<T> serviceClass) {
        List<? extends Provider<? extends Handler>> handlerProviders = jaxwsHandlerClasses.stream().map(binder::getProvider).collect(Collectors.toList());
        TypeLiteral<Optional<HostnameVerifier>> optionalHostnameVerifierType = SkifUtil.typeLiteral(Optional.class, HostnameVerifier.class);
        JaxWsServiceProvider<T> jaxWsServiceProvider = new JaxWsServiceProvider<>(
                poolConfigBuilder.build(),
                serviceClass,
                portTypeToServiceNameStrategy,
                serviceNameToWsdlLocationStrategy,
                handlerProviders,
                binder.getProvider(WebServiceExceptionMapper.class),
                binder.getProvider(ServerUrlHolder.class),
                binder.getProvider(Key.get(optionalHostnameVerifierType)));
        bind(serviceClass).toProvider(jaxWsServiceProvider).in(Singleton.class);
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
