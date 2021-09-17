package no.statkart.skif.skiftest.wsapi.service.test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.sun.xml.ws.developer.JAXWSProperties;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.SkifClientConfiguration;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.skiftest.wsapi.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.service.test1.Test1ServiceWS;
import no.statkart.skif.skiftest.wsapi.service.test3.Test3Service;
import no.statkart.skif.skiftest.wsapi.service.test3.Test3ServiceWS;
import no.statkart.skif.util.NullHostnameVerifier;
import no.statkart.skif.util.testsupport.SkifTestConfigurationAccessor;
import org.assertj.core.api.Fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;

/**
 * Veldig enkel klienttest som tester håndtering av exceptions fra Test3Service uten bruk av mapping rammeverket.
 *
 * @author Henrik Fredholm
 * @since 3.1
 */
@Test(groups = "server-required")
public class Test3ServiceWSTestJEE {
    private Injector injector;
    private SkifTestContext skifTestContext;

    private final SkifTestConfigurationAccessor config = new SkifTestConfigurationAccessor(new SkifClientConfiguration());

    private ModuleConfiguration createClientConfiguration() {
        return new DefaultModuleConfiguration()
                .setStrategyFactory(new ClientModuleStrategyFactory())
                .setServiceMode(ServiceMode.JEE);
    }

    /**
     * Setup JAX-WS klient mot remote server med hvor Guice bindinger for Web Servicen konfigureres manuelt
     */
    @BeforeClass
    public void setUp() {
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
                    <T> Test3Service provideJaxWSService(LoginUserHolder loginUserHolder, ServerUrlHolder serverUrlHolder, @Nullable HostnameVerifier hostnameVerifier) {
                        Test3ServiceWS endpoint = new Test3ServiceWS();
                        Test3Service port = endpoint.getTest3ServicePort();
                        BindingProvider bindings = (BindingProvider) port;
                        final LoginUser loginUser = loginUserHolder.get();
                        if (loginUser != null) {
                            bindings.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, loginUser.getUsername());
                            bindings.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, loginUser.getPassword());
                        }
                        final String serverUrl = serverUrlHolder.get();
                        if (serverUrl != null) {
                            String serviceEndpointUrl = serverUrl + getWebServiceContextPath(Test3ServiceWS.class);
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
                            throw new ConfigurationException("WebService targetnamesspace følger ikke forventet mønster (http://dns-adresse/fast/wsapi/service/...): " + annotation.targetNamespace());
                        }
                    }

                });
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser(config.getUsername(), config.getPassword()));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(config.getServerUrl());
        skifTestContext = new SkifTestContext();
        skifTestContext.setLocale("nb_NO");
    }

    @Test
    public void callWithNoException() throws ServiceException {
        final Test3Service instance = injector.getInstance(Test3Service.class);
        B b = new B();
        b.setText("foo");
        assertEquals(instance.b2A(b, skifTestContext).getText(), "foo");
    }

    @Test
    public void callWithException() throws ServiceException {
        final Test3Service instance = injector.getInstance(Test3Service.class);
        B b = new B();
        b.setText("foo");
        try {
            instance.testExceptionThrowing("do-throw-exception", "foo", skifTestContext);
            Fail.failBecauseExceptionWasNotThrown(SimpleException.class);
        } catch (SimpleException e) {
            assertEquals(e.getMessage(), "foo");
            assertEquals(e.getFaultInfo().getInfoField(), "foo");
        }
    }
}
