package no.statkart.skif.skiftest.service.test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.service.test1.Test1Service;
import no.statkart.skif.skiftest.service.test2.Test2Service;
import no.statkart.skif.skiftest.service.test3.Test3Service;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper2;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper3;
import no.statkart.skif.util.NullHostnameVerifier;
import no.statkart.skif.util.testsupport.SkifTestConfigurationAccessor;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(groups = "server-required")
public class Test2And3ServiceTestJEE {
    private Injector injector;

    SkifTestConfigurationAccessor config  = new SkifTestConfigurationAccessor(new SkifConfiguration());

    private ModuleConfiguration createClientConfiguration() {
        return new DefaultModuleConfiguration()
                .setStrategyFactory(new ClientModuleStrategyFactory())
                .setServiceMode(ServiceMode.JEE);
    }

    /**
     * Java klient over JAX-WS mot remote server hvor Guice bindinger alle services konfigureres i samme modul.
     */
    public void testServicesInSameModule() {
        ModuleConfiguration clientCfg = createClientConfiguration();
        final Set<Class<? extends Object>> services = new HashSet<Class<? extends Object>>();
        services.add(Test2Service.class);
        services.add(Test3Service.class);

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new RemoteServiceModule(clientCfg, services, new SkifTestMapper().getMapping())
                        .setServiceContextMapperClass(SkifTestServiceContextMapper.class));
        setlogin();
        callTestServices();
    }

    /**
     * Java klient over JAX-WS  mot remote server hvor Guice bindinger for servicene er delt over flere moduler
     * og hvor hver module bruker egen mapping2 instans og ServiceContext klasse. Den ene modulen bruker også
     * en egen CallServiceChainFactory.
     */
    public void testServicesInDifferentModules() {
        ModuleConfiguration clientCfg = createClientConfiguration();
        final Set<Class<? extends Object>> services1 = new HashSet<Class<? extends Object>>();
        services1.add(Test1Service.class);
        final Set<Class<? extends Object>> services2 = new HashSet<Class<? extends Object>>();
        services2.add(Test2Service.class);
        final Set<Class<? extends Object>> services3 = new HashSet<Class<? extends Object>>();
        services3.add(Test3Service.class);

        final RemoteServiceModule remoteServiceModule = new RemoteServiceModule(clientCfg, services2, new SkifTestMapper2().getMapping())
                .setServiceContextMapperClass(SkifTestServiceContextMapper.class);

        remoteServiceModule.getStrategy(ServiceMode.JEE).setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(CallChainFactory.class));

        injector = Guice.createInjector(
                new RemoteServerModule(clientCfg)
                        .setHostnameVerifierClass(NullHostnameVerifier.class),
                new RemoteServiceModule(clientCfg, services1, new IdentityMapper().getMapping()).setServiceContextMapperClass(SkifTestServiceContextMapper.class),
                remoteServiceModule,
                new RemoteServiceModule(clientCfg, services3, new SkifTestMapper3().getMapping())
                        .setServiceContextMapperClass(SkifTestServiceContextMapper.class));
        setlogin();
        callTest1Service();
        callTestServices();
    }

    private void callTest1Service() {
        final Test1Service test1Service = injector.getInstance(Test1Service.class);
        final String s = test1Service.helloWorld("Henrik");
        assertEquals(s, "Hello1: Henrik");
    }

    private void callTestServices() {
        final Test2Service test2Service = injector.getInstance(Test2Service.class);
        A a = new A();
        a.setText("a1");
        final B b = test2Service.a2B(a);
        assertEquals(b.getText(), "a1");

        final Test3Service test3Service = injector.getInstance(Test3Service.class);
        final A a2 = test3Service.b2A(b);
        assertEquals(a2.getText(), "a1");

    }

    private void setlogin() {
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser(config.getUsername(), config.getPassword()));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(config.getServerUrl());
    }

}

class CallChainFactory<S> extends ClientCallServiceChainFactoryJEE<S> {

    @Inject
    public CallChainFactory(TypeLiteral<S> type, TerminatingProxyHandler<S> proxyHandler) {
        super(type, proxyHandler);
    }

    @Override
    public ProxyHandler<S> createChain() {
        return super.createChain();    //To change body of overridden methods use File | Settings | File Templates.
    }
}



