package no.statkart.skif.skiftest.wsapi.service.test;

import com.google.inject.*;
import com.sun.xml.ws.developer.JAXWSProperties;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.WSRemoteServiceModule;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import no.statkart.skif.service.ws.JaxWsServiceWithDynamicRequestContextProvider;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.skiftest.wsapi.service.test1.Test1Service;
import no.statkart.skif.skiftest.wsapi.service.test1.Test1ServiceWS;
import no.statkart.skif.skiftest.wsapi.service.test2.Test2Service;
import no.statkart.skif.util.NullHostnameVerifier;
import no.statkart.skif.util.testsupport.SkifTestConfigurationAccessor;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceClient;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(groups = "server-required")
public class Test1ServiceWSTestJEE {
    private Injector injector;

    private SkifTestConfigurationAccessor config = new SkifTestConfigurationAccessor(new SkifConfiguration());

    private ModuleConfiguration createClientConfiguration() {
        return new DefaultModuleConfiguration()
                .setStrategyFactory(new ClientModuleStrategyFactory())
                .setServiceMode(ServiceMode.JEE);
    }

    /**
     * JAX-WS klient mot remote server med hvor Guice bindinger for Web Servicen konfigureres manuelt
     */
    @Test(groups = "server-required")
    public void testJaxWsClientServiceCreateManually() {
        ModuleConfiguration clientCfg = createClientConfiguration();

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new AbstractModule() {

                    @Override
                    protected void configure() {
                        requireBinding(LoginUserHolder.class);
                        requireBinding(ServerUrlHolder.class);
                        requireBinding(HostnameVerifier.class);
                    }

                    @Provides
                    <T> no.statkart.skif.skiftest.wsapi.service.test1.Test1Service provideJaxWSService(LoginUserHolder loginUserHolder, ServerUrlHolder serverUrlHolder, @Nullable HostnameVerifier hostnameVerifier) {
                        Test1ServiceWS endpoint = new Test1ServiceWS();
                        no.statkart.skif.skiftest.wsapi.service.test1.Test1Service port = endpoint.getTest1ServicePort();
                        BindingProvider bindings = (BindingProvider) port;
                        final LoginUser loginUser = loginUserHolder.get();
                        if (loginUser != null) {
                            bindings.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, loginUser.getUsername());
                            bindings.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, loginUser.getPassword());
                        }
                        final String serverUrl = serverUrlHolder.get();
                        if (serverUrl != null) {
                            String serviceEndpointUrl = serverUrl + getWebServiceContextPath(Test1ServiceWS.class);
                            bindings.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpointUrl);
                        }
                        if (hostnameVerifier != null) {
                            bindings.getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, hostnameVerifier);
                        }
                        return port;
                    }

                    private String getWebServiceContextPath(Class<? extends javax.xml.ws.Service> wsClass) {
                        // Matcher http://skif.statkart.no/skiftest/wsapi/service/...
                        Pattern pattern = Pattern.compile(".*://[^/]*/(.*)/service/.*");
                        WebServiceClient annotation = wsClass.getAnnotation(WebServiceClient.class);
                        if (annotation == null) {
                            throw new ConfigurationException("WebService har ikke noen @WebserviceClient annotation: " + wsClass.getName());
                        }
                        Matcher m = pattern.matcher(annotation.targetNamespace());
                        if (m.matches()) {
                            String contextPath = m.group(1);
                            return "/" + contextPath + "/" + wsClass.getSimpleName();
                        } else {
                            throw new no.statkart.skif.exception.ConfigurationException("WebService targetnamesspace følger ikke forventet mønster (http://dns-adresse/fast/wsapi/service/...): " + annotation.targetNamespace());
                        }
                    }

                });

        callWSService();
    }

    /**
     * JAX-WS klient mot remote server hvor Guice binding av Web Servicen konfigureres manuelt vha en JAX-WS provider
     */

    public void testJaxWsClientServiceCreateUsingProvider() throws Exception {
        ModuleConfiguration clientCfg = createClientConfiguration();

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        requireBinding(LoginUserHolder.class);
                        requireBinding(ServerUrlHolder.class);
                        requireBinding(HostnameVerifier.class);
                        bind(no.statkart.skif.skiftest.wsapi.service.test1.Test1Service.class).toProvider(new TypeLiteral<JaxWsServiceProvider<Test1Service>>() {
                        });
                    }
                });

        callWSService();
    }

    /**
     * JAX-WS klient mot remote server hvor Guice binding av Web Servicen konfigureres manuelt vha en JAX-WS provider som
     * støtter dynamisk endringer username, password og url
     * som
     */
    @Test(groups = "server-required")
    public void testJaxWsClientServiceCreateUsingProviderWithDynamicRequestContext() throws Exception {
        ModuleConfiguration clientCfg = createClientConfiguration();

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(no.statkart.skif.skiftest.wsapi.service.test1.Test1Service.class).toProvider(new TypeLiteral<JaxWsServiceWithDynamicRequestContextProvider<Test1Service>>() {
                        });
                    }
                });

        final Test1Service instance = injector.getInstance(Test1Service.class);
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(config.getServerUrl());

        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);

        loginUserHolder.set(new LoginUser(config.getUsername(), config.getPassword()));
        SkifTestContext skifTestContext = new SkifTestContext();
        skifTestContext.setLocale("nb_NO");
        assertEquals(instance.helloWorld("Henrik", skifTestContext), "Hello1: Henrik");
        loginUserHolder.set(new LoginUser(config.getTestUser(), config.getTestUserPassword()));
        assertEquals(instance.helloWorld("Henrik", skifTestContext), "Hello1: Henrik");

        try {
            loginUserHolder.set(new LoginUser("unknown_user", "wrongPassword"));
            assertEquals(instance.helloWorld("Henrik", skifTestContext), "Hello1: Henrik");
            fail("Forventet exception");
        } catch (com.sun.xml.ws.client.ClientTransportException e) {
            Assert.assertEquals(e.getArguments()[0], 401);
        }

    }

    /**
     * JAX-WS klient mot remote server hvor Guice bindinger produseres hva en WSRemoteServiceModule
     */
    @Test(groups = "server-required")
    public void testJaxWsClientServiceCreateUsingWSRemoteModule() {
        ModuleConfiguration clientCfg = createClientConfiguration();
        final List<Class<? extends Object>> serviceClasses = Arrays.asList(Test1Service.class, Test2Service.class);

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new WSRemoteServiceModule(clientCfg, serviceClasses)
        );
        final Test1Service instance = injector.getInstance(Test1Service.class);
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(config.getServerUrl());

        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser(config.getUsername(), config.getPassword()));
        SkifTestContext skifTestContext = new SkifTestContext();
        skifTestContext.setLocale("nb_NO");
        assertEquals(instance.helloWorld("Henrik", skifTestContext), "Hello1: Henrik");
        loginUserHolder.set(new LoginUser(config.getTestUser(), config.getTestUserPassword()));
        assertEquals(instance.helloWorld("Henrik", skifTestContext), "Hello1: Henrik");
    }

    private void callWSService() {
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser(config.getUsername(), config.getPassword()));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(config.getServerUrl());
        final Test1Service instance = injector.getInstance(Test1Service.class);
        SkifTestContext skifTestContext = new SkifTestContext();
        skifTestContext.setLocale("nb_NO");
        assertEquals(instance.helloWorld("Henrik", skifTestContext), "Hello1: Henrik");
    }

}
