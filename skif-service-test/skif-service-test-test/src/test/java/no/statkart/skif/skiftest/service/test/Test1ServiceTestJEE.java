package no.statkart.skif.skiftest.service.test;

import com.google.common.collect.Lists;
import com.google.inject.*;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.service.annotation.Call;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.chain.ServiceChainFactories;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.D2WAdapterWithServiceContextMapperProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;
import no.statkart.skif.skiftest.service.test1.Test1Service;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.util.NullHostnameVerifier;
import no.statkart.skif.util.testsupport.SkifTestConfigurationAccessor;
import org.testng.annotations.Test;

import javax.net.ssl.HostnameVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(groups = "server-required")
public class Test1ServiceTestJEE {
    private Injector injector;
    private SkifTestConfigurationAccessor config = new SkifTestConfigurationAccessor(new SkifConfiguration());

    private ModuleConfiguration createClientConfiguration() {
        return new DefaultModuleConfiguration()
                .setStrategyFactory(new ClientModuleStrategyFactory())
                .setServiceMode(ServiceMode.JEE);
    }

    /**
     * Java klient over JAX-WS mot remote server hvor Guice bindinger for servicen konfigureres manuelt
     */
    public void testJaxWsClientServiceCreateUsingProvider() throws Exception {
        ModuleConfiguration clientCfg = createClientConfiguration();

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class).setServiceContextClass(SkifTestServiceContext.class),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        requireBinding(LoginUserHolder.class);
                        requireBinding(ServerUrlHolder.class);
                        requireBinding(HostnameVerifier.class);
                        // Bind Web Service interface
                        bind(no.statkart.skif.skiftest.wsapi.service.test1.Test1Service.class).toProvider(new TypeLiteral<JaxWsServiceProvider<no.statkart.skif.skiftest.wsapi.service.test1.Test1Service>>() {
                        });

                        bind(new TypeLiteral<ServiceContextMapper<?>>(){}).to(SkifTestServiceContextMapper.class);

                        // Bind Mapping
                        bind(Mapping.class).toInstance(new IdentityMapper().getMapping());

                        // Bind CallServiceChainFactory and support required support classes
                        bind(new TypeLiteral<TerminatingProxyHandler<no.statkart.skif.skiftest.service.test1.Test1Service>>() {}).
                                to(new TypeLiteral<D2WAdapterWithServiceContextMapperProxyHandler<Test1Service, no.statkart.skif.skiftest.wsapi.service.test1.Test1Service>>() {});//
                        ServiceChainFactories.multibindFactory(binder(), CallServiceChainFactory.class, no.statkart.skif.skiftest.service.test1.Test1Service.class, ClientCallServiceChainFactoryJEE.class);

                        // Bind ServiceProvider
                        bind(no.statkart.skif.skiftest.service.test1.Test1Service.class).toProvider(new TypeLiteral<ServiceProvider<no.statkart.skif.skiftest.service.test1.Test1Service>>(){});
                        bind(new TypeLiteral<List<ChainedProxyHandler<Test1Service>>>(){}).annotatedWith(Call.class).toProvider(
                                new Provider<List<ChainedProxyHandler<Test1Service>>>() {
                                    @Inject
                                    Injector injector;

                                    @Override
                                    public List<ChainedProxyHandler<Test1Service>> get() {
                                        return Lists.newArrayList();
                                    }
                                });

                    }
                });

        callTest1Service();
    }

    /**
     * Java klient over JAX-WS  mot remote server hvor Guice bindinger for servicen konfigureres via en RemoteServiceModule
     */
    public void testJaxWsClientServiceCreateUsingModule() throws Exception {
        ModuleConfiguration clientCfg = createClientConfiguration();
        final Set<Class<? extends Object>> services = new HashSet<Class<? extends Object>>();
        services.add(no.statkart.skif.skiftest.service.test1.Test1Service.class);

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new RemoteServiceModule(clientCfg, services, new IdentityMapper().getMapping()).setServiceContextMapperClass(SkifTestServiceContextMapper.class)
        );
        callTest1Service();
    }

    private void callTest1Service() {
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser(config.getUsername(), config.getPassword()));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(config.getServerUrl());

        final no.statkart.skif.skiftest.service.test1.Test1Service test1Service = injector.getInstance(no.statkart.skif.skiftest.service.test1.Test1Service.class);
        assertEquals(test1Service.helloWorld("Henrik"), "Hello1: Henrik");
    }
}
