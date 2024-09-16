package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.D2WAdapterProxyHandler;
import no.statkart.skif.service.proxy.D2WAdapterWithServiceContextMapperProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.ws.WebServiceExceptionMapper;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.jws.WebService;
import javax.net.ssl.HostnameVerifier;
import javax.xml.namespace.QName;
import jakarta.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RemoteServiceModuleStrategyJEE extends RemoteServiceModuleStrategy implements JaxWsModule.JaxWsModuleStrategyJEE {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceModuleStrategyJEE.class);

    private final Class<? extends D2WAdapterProxyHandler> d2WAdapterProxyHandlerClass;
    private final JaxWsServiceProvider.PoolConfig.Builder poolConfigBuilder;
    private final List<Class<? extends Handler>> jaxwsHandlerClasses;
    private Function<? super String, String> portTypeToServiceNameStrategy;
    private Function<? super QName, String> serviceNameToWsdlLocationStrategy;

    public RemoteServiceModuleStrategyJEE() {
        this(D2WAdapterWithServiceContextMapperProxyHandler.class);
    }
    public RemoteServiceModuleStrategyJEE(Class<? extends D2WAdapterProxyHandler> d2WAdapterProxyHandlerClass) {
        this(d2WAdapterProxyHandlerClass, JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_JAXWS_HANDLER_CLASSES);
    }

    public RemoteServiceModuleStrategyJEE(Class<? extends D2WAdapterProxyHandler> d2WAdapterProxyHandlerClass, Iterable<Class<? extends Handler>> jaxwsHandlerClasses) {
        this.d2WAdapterProxyHandlerClass = Objects.requireNonNull(d2WAdapterProxyHandlerClass, "d2WAdapterProxyHandlerClass");
        this.jaxwsHandlerClasses = StreamSupport
                .stream(Objects.requireNonNull(jaxwsHandlerClasses, "jaxwsHandlerClasses").spliterator(), false)
                .collect(toList());
        this.poolConfigBuilder = JaxWsServiceProvider.PoolConfig.builder();
        setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ClientCallServiceChainFactoryJEE.class));
        portTypeToServiceNameStrategy = JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_PORT_TYPE_TO_SERVICE_NAME_STRATEGY;
        serviceNameToWsdlLocationStrategy = JaxWsModule.JaxWsModuleStrategyJEE.DEFAULT_SERVICE_NAME_TO_WSDL_LOCATION_STRATEGY;
    }

    @Override
    public void requireBindings(Binder binder) {
        binder.install(JaxWsModule.Common.getInstance());
    }

    @Override
    public <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service) {
        Class<?> webServiceClass = findWebServicePortClass(service);
        bindService(outerBinder, innerBinder, service, webServiceClass);
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
        this.portTypeToServiceNameStrategy = Objects.requireNonNull(portTypeToServiceNameStrategy);
    }

    @Override
    public void setServiceNameToWsdlLocationStrategy(Function<? super QName, String> serviceNameToWsdlLocationStrategy) {
        this.serviceNameToWsdlLocationStrategy = Objects.requireNonNull(serviceNameToWsdlLocationStrategy);
    }

    @Override
    public List<Class<? extends Handler>> getJaxwsHandlerClasses() {
        return jaxwsHandlerClasses;
    }

    /**
     * Finner riktig webservice port klasse basert på navnekonvensjon:
     * <ul>
     *     <li>det finnes en portklasse/interface med samme navn som tjenesteklassen, eller</li>
     *     <li>det finnes en portklasse/interface med samme navn som tjenesten etterfulgt av WSI</li>
     * </ul>
     * <p/>
     * Portklassen/interfacet må være annotert med @{@linkplain WebService}
     * <p/>
     * Transformerer java pakkenavn basert på {@link #classWSPackageMappings}<
     */
    protected Class<?> findWebServicePortClass(Class<?> domainServiceClass) {
        String serviceClassname = domainServiceClass.getName();
        Class<?> portClass = null;

        List<String> mapingsTried = new ArrayList<>();
        List<String> classNamesTried = new ArrayList<>();

        for (String classPackageMapping : classWSPackageMappings) {
            final String[] mapping = classPackageMapping.split(":");
            if (mapping.length != 2) {
                throw new ConfigurationException("Error in Web Service classmapping. Expected format \"fromPackage:toPackage\":" + classPackageMapping);
            }
            final String fromPackage = mapping[0];
            final String toPackage = mapping[1];
            final Matcher matcher = Pattern.compile(Matcher.quoteReplacement(fromPackage)).matcher(serviceClassname);
            String portClassName = matcher.replaceFirst(toPackage);

            // Sjekk om vi har en kombinasjon av port interface og klientstub etter konvensjonen at
            // port interfacet har samme navn som tjeneste, og generert service klient har samme navn som tjeneste + WS
            Class<?> portClassCandidate = findPortClassForPortClassName(portClassName);

            // Sjekk om vi har ett port interface med navnet tjenestnavn + WSI annotert med @WebService
            if (portClassCandidate == null) {
                classNamesTried.add(portClassName);
                String alternativePortClassName = portClassName.concat("WSI");
                portClassCandidate = findPortClassForPortClassName(alternativePortClassName);
                if (portClassCandidate == null) {
                    classNamesTried.add(alternativePortClassName);
                }
            }

            if (portClassCandidate == null) {
                mapingsTried.add(classPackageMapping);
            } else if (portClass == null) {
                portClass = portClassCandidate;
            } else {
                throw new ImplementationException(String.format(
                        "Multiple port classes found for %s:%n\t%s%n\t%s",
                        serviceClassname,
                        portClass.getName(),
                        portClassCandidate.getName()));
            }
        }

        if (portClass == null) {
            throw new ConfigurationException("Could not find Web Service port class for service: " + domainServiceClass.getName() + " using packagemappings: " + mapingsTried + ". The following Web Service port classnames where tried: " + classNamesTried);
        }
        return portClass;
    }

    protected <S,W> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service, Class<W> webService) {
        TypeLiteral<ServiceProvider<S>> remoteServiceProviderType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        TypeLiteral<TerminatingProxyHandler<S>> terminatingProxyHandlerType = SkifUtil.typeLiteral(TerminatingProxyHandler.class, service);
        TypeLiteral<D2WAdapterProxyHandler<S,W>> d2WAdapterProxyHandlerType = SkifUtil.typeLiteral(d2WAdapterProxyHandlerClass, service, webService);

        outerBinder.bind(service).toProvider(remoteServiceProviderType);

        List<Provider<? extends Handler>> handlerProviders = jaxwsHandlerClasses.stream().map(outerBinder::getProvider).collect(toList());
        TypeLiteral<Optional<HostnameVerifier>> optionalHostnameVerifierType = SkifUtil.typeLiteral(Optional.class, HostnameVerifier.class);
        JaxWsServiceProvider<W> jaxWsServiceProvider = new JaxWsServiceProvider<>(
                poolConfigBuilder.build(),
                webService,
                portTypeToServiceNameStrategy,
                serviceNameToWsdlLocationStrategy,
                handlerProviders,
                innerBinder.getProvider(WebServiceExceptionMapper.class),
                innerBinder.getProvider(ServerUrlHolder.class),
                innerBinder.getProvider(Key.get(optionalHostnameVerifierType)));
        outerBinder.bind(webService).toProvider(jaxWsServiceProvider).in(Singleton.class);

        innerBinder.bind(terminatingProxyHandlerType).to(d2WAdapterProxyHandlerType);
        innerBinder.expose(terminatingProxyHandlerType);
    }

    protected final @Nullable Class<?> findPortClassForPortClassName(String portClassName) {
        Class<?> portClassCandidate;
        try {
            portClassCandidate = Class.forName(portClassName, false, getClass().getClassLoader());
            WebService annotation = portClassCandidate.getAnnotation(WebService.class);
            if (annotation == null) {
                logger.debug("Detected port type candidate {} is not a port type: Not annotated with @WebService", portClassCandidate.getName());
                return null;
            }
            if (!annotation.serviceName().equals("") ||
                    !annotation.portName().equals("") ||
                    !annotation.endpointInterface().equals("")) {
                logger.debug("Detected port type candidate {} is not a port type: @WebService annotation indicates an service implementation", portClassCandidate.getName());
                return null;
            }
        } catch (ClassNotFoundException|ClassCastException e) {
            logger.trace("Port type {} not found", portClassName);
            portClassCandidate = null;
        }
        return portClassCandidate;
    }


}
