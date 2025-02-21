package no.statkart.skif.service.ws;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServerUrlHolder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import jakarta.inject.Provider;
import jakarta.jws.WebService;
import javax.net.ssl.HostnameVerifier;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * Guice Provider implementasjon for å opprette en JAX-WS klient for en gitt porttype {@code <T>}. Provideren anvender en
 * JaxWS builder klasse som automatisk avleder Web service endpoint klasse og Web service context path fra porttype klassen.
 *
 */
public class JaxWsServiceProvider<S> implements Provider<S> {

    private final S clientProxy;

    public static class PoolConfig {
        public static Builder builder() { return new Builder(); }
        private final int maxIdle;
        private final int maxTotal;
        private final int minIdle;

        public int getMaxIdle() {
            return maxIdle;
        }
        public int getMaxTotal() {
            return maxTotal;
        }
        public int getMinIdle() {
            return minIdle;
        }

        private PoolConfig(int maxIdle, int maxTotal, int minIdle) {
            this.maxIdle = maxIdle;
            this.maxTotal = maxTotal;
            this.minIdle = minIdle;
        }

        public static class Builder {
            private int maxIdle = 1;
            private int maxTotal = Runtime.getRuntime().availableProcessors();
            private int minIdle = 1;

            private Builder() {}

            public Builder withMaxIdle(int maxIdle) {
                this.maxIdle = maxIdle;
                return this;
            }

            public Builder withMaxTotal(int maxTotal) {
                this.maxTotal = maxTotal;
                return this;
            }

            public Builder withMinIdle(int minIdle) {
                this.minIdle = minIdle;
                return this;
            }

            public PoolConfig build() {
                return new PoolConfig(maxIdle, maxTotal, minIdle);
            }
        }
    }

    public JaxWsServiceProvider(
            PoolConfig poolConfig,
            Class<S> serviceEndpointInterface,
            Function<? super String, String> portTypeToServiceNameStrategy,
            Function<? super QName, String> serviceNameToWsdlLocationStrategy,
            @SuppressWarnings("rawtypes")
            Iterable<? extends Provider<? extends Handler>> handlerProviders,
            Provider<WebServiceExceptionMapper> jaxWsExceptionHandlerProvider,
            Provider<? extends ServerUrlHolder> serverUrlHolderProvider,
            Provider<? extends Optional<? extends HostnameVerifier>> hostnameVerifierOptionProvider
    ) {
        Service serviceEndpointClientFactory = createServiceEndpointClientFactory(
                serviceEndpointInterface.getClassLoader(),
                serviceEndpointInterface,
                portTypeToServiceNameStrategy,
                serviceNameToWsdlLocationStrategy,
                handlerProviders);

        GenericObjectPoolConfig<S> config = new GenericObjectPoolConfig<>();
        config.setJmxEnabled(false);
        GenericObjectPool<S> pool = new GenericObjectPool<>(
                new PortPooledObjectFactory<>(
                serviceEndpointClientFactory,
                serviceEndpointInterface,
                serverUrlHolderProvider,
                hostnameVerifierOptionProvider),
                config
        );
        pool.setMinIdle(poolConfig.getMinIdle());
        pool.setMaxIdle(poolConfig.getMaxIdle());
        pool.setMaxTotal(poolConfig.getMaxTotal());

        clientProxy = new JaxWsRequestInvokeProxyHandler<>(pool, jaxWsExceptionHandlerProvider)
                .buildProxy(serviceEndpointInterface);
    }

    @Override
    public final S get() {
        return clientProxy;
    }

    private static <S> Service createServiceEndpointClientFactory(
            ClassLoader cl,
            Class<S> serviceEndpointInterface,
            Function<? super String, String> portTypeToServiceNameStrategy,
            Function<? super QName, String> serviceNameToWsdlLocationStrategy,
            @SuppressWarnings("rawtypes")
            Iterable<? extends Provider<? extends Handler>> handlerProviders
    ) {
        WebService annotation = serviceEndpointInterface.getAnnotation(WebService.class);
        if (annotation == null) {
            throw new ImplementationException(String.format("Port class not annotated with @WebService: %s", serviceEndpointInterface));
        }
        String portTypeName = annotation.name().equals("")
                ? serviceEndpointInterface.getSimpleName()
                : annotation.name();
        String serviceName = portTypeToServiceNameStrategy.apply(portTypeName);

        Class<? extends Service> serviceEndpointFactoryClass;
        Constructor<? extends Service> serviceEndpointFactoryConstructor;
        try {
            Package pkg = serviceEndpointInterface.getPackage();
            String serviceEndpointFactoryClassName;
            if (pkg == null || pkg.getName().isEmpty()) {
                serviceEndpointFactoryClassName = serviceName;
            } else {
                serviceEndpointFactoryClassName = pkg.getName() + '.' + serviceName;
            }
            serviceEndpointFactoryClass = Class.forName(serviceEndpointFactoryClassName, false, cl).asSubclass(Service.class);
            try {
                serviceEndpointFactoryConstructor = serviceEndpointFactoryClass.getConstructor();
            } catch (NoSuchMethodException e) {
                if (serviceEndpointFactoryClass.isAnnotationPresent(WebServiceClient.class)) {
                    throw new ImplementationException("WebService client has no public no-arg constructor", e);
                }
                throw e;
            }
        } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException e) {
            serviceEndpointFactoryConstructor = null;
        }

        Service serviceEndpointFactory = null;
        if (serviceEndpointFactoryConstructor != null) {
            try {
                serviceEndpointFactory = serviceEndpointFactoryConstructor.newInstance();
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new ImplementationException(e);
            }
        }

        if (serviceEndpointFactory == null) {
            String targetNamespace = annotation.targetNamespace();
            if (targetNamespace.equals("")) {
                throw new ImplementationException(String.format("Target namespace must be specified: %s", serviceEndpointInterface.getName()));
            }
            QName serviceQualifiedName = new QName(targetNamespace, serviceName);

            String wsdlLocation = (annotation.wsdlLocation().equals("")
                    ? serviceNameToWsdlLocationStrategy.apply(serviceQualifiedName)
                    : annotation.wsdlLocation()).replaceFirst("^/+", "");

            URL wsdlUrl;
            try {
                wsdlUrl = Collections
                        .list(cl.getResources(wsdlLocation))
                        .stream()
                        .reduce((a, b) -> {
                            throw new ImplementationException(String.format(
                                    "Multiple instances of WSDL %s on classpath:%n\t%s%n\t%s",
                                    wsdlLocation, a, b));
                        })
                        .orElseThrow(() -> new ImplementationException(String.format(
                                "WSDL for %s not found: %s", serviceEndpointInterface, wsdlLocation)));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            serviceEndpointFactory = Service.create(wsdlUrl, serviceQualifiedName);
        }

        List<Provider<? extends Handler>> handlerProvidersList = StreamSupport
                .stream(Objects.requireNonNull(handlerProviders, "handlerProviders").spliterator(), false)
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
        HandlerResolver originalHandlerResolver = serviceEndpointFactory.getHandlerResolver();
        if (originalHandlerResolver != null) {
            serviceEndpointFactory.setHandlerResolver(portInfo -> {
                List<Handler> originalHandlers = originalHandlerResolver.getHandlerChain(portInfo);
                List<Handler> handlerChain = new ArrayList<>(originalHandlers.size() + handlerProvidersList.size());
                handlerChain.addAll(originalHandlers);
                handlerProvidersList.stream().map(Provider::get).forEachOrdered(handlerChain::add);
                return handlerChain;
            });
        } else {
            serviceEndpointFactory.setHandlerResolver(portInfo -> handlerProvidersList.stream().map(Provider::get).collect(toList()));
        }

        return serviceEndpointFactory;
    }

    private static class PortPooledObjectFactory<S> extends BasePooledObjectFactory<S> {
        private static final String SERVICE_PATH_ELEMENT = "/service";

        private final Service serviceEndpointClientFactory;
        private final Class<S> serviceEndpointInterface;
        private final Provider<? extends ServerUrlHolder> serverUrlHolderProvider;
        private final Provider<? extends Optional<? extends HostnameVerifier>> hostnameVerifierOptionProvider;

        public PortPooledObjectFactory(
                Service serviceEndpointClientFactory,
                Class<S> serviceEndpointInterface,
                Provider<? extends ServerUrlHolder> serverUrlHolderProvider,
                Provider<? extends Optional<? extends HostnameVerifier>> hostnameVerifierOptionProvider
        ) {
            this.serviceEndpointClientFactory = serviceEndpointClientFactory;
            this.serviceEndpointInterface = serviceEndpointInterface;
            this.serverUrlHolderProvider = serverUrlHolderProvider;
            this.hostnameVerifierOptionProvider = hostnameVerifierOptionProvider;
        }

        @Override
        public S create() {
            QName serviceQName = serviceEndpointClientFactory.getServiceName();
            String endpointPathFromServiceQName = endpointPathFromServiceQName(serviceQName);

            S port = serviceEndpointClientFactory.getPort(serviceEndpointInterface);
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();

            String currentServerUrl = serverUrlHolderProvider.get().get();
            while (currentServerUrl.endsWith("/")) {
                currentServerUrl = currentServerUrl.substring(0, currentServerUrl.length() - 1);
            }
            String serviceEndpointUrl = currentServerUrl + endpointPathFromServiceQName + '/' + serviceQName.getLocalPart();
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpointUrl);

            hostnameVerifierOptionProvider.get().ifPresent(hostnameVerifier ->
                    requestContext.put("com.sun.xml.ws.transport.https.client.hostname.verifier", hostnameVerifier)
            );

            return port;
        }

        @Override
        public PooledObject<S> wrap(S obj) {
            return new DefaultPooledObject<>(obj);
        }

        private static String endpointPathFromServiceQName(QName serviceQName) {
            URI namespaceURI = URI.create(serviceQName.getNamespaceURI());
            String namespacePath = namespaceURI.getPath();
            String endpointPath;
            int indexOfService = namespacePath.lastIndexOf(SERVICE_PATH_ELEMENT);
            if (indexOfService > 0 && (namespacePath.length() - indexOfService == SERVICE_PATH_ELEMENT.length() || namespacePath.charAt(indexOfService + SERVICE_PATH_ELEMENT.length()) == '/' )) {
                endpointPath = namespacePath.substring(0, indexOfService);
            } else {
                endpointPath = namespacePath;
            }
            return endpointPath;
        }
    }
}
