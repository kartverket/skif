package no.statkart.skif.service.module;

import com.google.inject.*;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.chain.ImplementationServiceChainFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.test.service.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;


/**
 * Configuration
 * - StrategyFactory
 * - Properties
 * - ServerMode
 * <p/>
 * <p/>
 * ModuleConfig
 * -StrategyFactory
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test
public class SingleVmModuleTest {
    private ModuleConfiguration clientCfg;
    private ModuleConfiguration serverCfg;
    private List<Class<? extends Object>> services;
    private List<Class<? extends Object>> services2;

    @BeforeMethod
    private void setUp() {
        clientCfg = new DefaultModuleConfiguration()
                .setStrategyFactory(new ClientModuleStrategyFactory())
                .setServiceMode(ServiceMode.SINGLE_VM);

        serverCfg = new DefaultModuleConfiguration()
                .setStrategyFactory(new ServerModuleStrategyFactory())
                .setServiceMode(ServiceMode.SINGLE_VM);

        // Simpel service gruppe
        services = new ArrayList<Class<? extends Object>>();
        services.add(Test1Service.class);
        services.add(Test2Service.class);

        // Servicegruppe med Tx annotasjoner og hvor services kaller hverandre
        services2 = new ArrayList<Class<? extends Object>>();
        services2.add(AService.class);
        services2.add(BService.class);
        services2.add(CService.class);
    }

    private Injector createServerInjector(List<Class<? extends Object>> services) {
        return Guice.createInjector(
                new ServerModule(serverCfg),
                new ServerServiceModule(serverCfg, services)
        );
    }

    private Injector createServerInjectorWithTxAnnotation(List<Class<? extends Object>> services) {
        final ServerServiceModule serverServiceModule = new ServerServiceModule(serverCfg, services);
        serverServiceModule.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactoryClass(AnnotatingEjbServiceChainFactory.class);


        return Guice.createInjector(
                new ServerModule(serverCfg),
                serverServiceModule
        );
    }

    private Injector createClientInjector(Injector serverInjector, List<Class<? extends Object>> services) {
        clientCfg.getConfiguration().setProperty(ConfigurationConstants.SINGLE_VM_SERVER_INJECTOR, serverInjector);
        return Guice.createInjector(
                new RemoteServerModule(clientCfg),
                new RemoteServiceModule(clientCfg, services, new IdentityMapper().getMapping())
        );
    }


    /**
     * ImplementationServiceChainFactory skal være singleton og det skal være mulig å opprette en instans av klassen
     * uten noe aktivt ServiceRequestScope.
     */
    public void testCreateImplementationServiceChainFactory() {
        final Injector serverInjector = createServerInjector(services);
        final ImplementationServiceChainFactory<Test1Service> factory = serverInjector.getInstance(
                Key.get(new TypeLiteral<ImplementationServiceChainFactory<Test1Service>>() {
                }));
        final ImplementationServiceChainFactory<Test1Service> factory2 = serverInjector.getInstance(
                Key.get(new TypeLiteral<ImplementationServiceChainFactory<Test1Service>>() {
                }));
        assertSame(factory, factory2, "ImplementationServiceChainFactory er ikke en singleton som forventet");
    }

    /**
     * Kall til createChain() kan normalt kun kaldes med et aktivt ServiceRequestScope
     */
    @Test(expectedExceptions = com.google.inject.ProvisionException.class)
    public void testCreateImplementationServiceChainFactory_NoScope() {
        final Injector serverInjector = createServerInjector(services);
        final ImplementationServiceChainFactory<Test2Service> factory = serverInjector.getInstance(
                Key.get(new TypeLiteral<ImplementationServiceChainFactory<Test2Service>>() {
                }));
        assertNotNull(factory);
        factory.createChain();
    }

    /**
     * Kall til createChain() kan normalt kun kaldes med et aktivt ServiceRequestScope
     */
    public void testCreateImplementationServiceChainFactory_WithScoped() {
        final Injector serverInjector = createServerInjector(services);
        final ImplementationServiceChainFactory<Test2Service> factory = serverInjector.getInstance(
                Key.get(new TypeLiteral<ImplementationServiceChainFactory<Test2Service>>() {
                }));
        assertNotNull(factory);
        final ServiceRequestScope scope = serverInjector.getInstance(ServiceRequestScope.class);
        scope.enter();
        assertNotNull(factory.createChain());
    }


    /**
     * EJBServiceChainFactory skal være singleton og det skal være mulig å opprette en instans av klassen
     * uten noe aktivt ServiceRequestScope.
     */
    public void testEJBServiceChainFactory() {
        final ProxyHandler<Test1Service> implementationServiceChain = mock(ProxyHandler.class);
        final ProxyHandler<Test1Service> implementationServiceChain2 = mock(ProxyHandler.class);
        final Injector serverInjector = createServerInjector(services);
        final EJBServiceChainFactory<Test1Service> factory = serverInjector.getInstance(
                Key.get(new TypeLiteral<EJBServiceChainFactory<Test1Service>>() {
                }));
        final EJBServiceChainFactory<Test1Service> factory2 = serverInjector.getInstance(
                Key.get(new TypeLiteral<EJBServiceChainFactory<Test1Service>>() {
                }));
        assertSame(factory, factory2, "EJBServiceChainFactory er ikke en singleton som forventet");
    }

    /**
     * CallServiceChainFactory på server skal være singleton og det skal være mulig å opprette en instans av klassen
     * uten noe aktivt ServiceRequestScope.
     */
    public void testCallServiceChainFactory_Server() {
        final Injector serverInjector = createServerInjector(services);
        final Set<CallServiceChainFactory<Test2Service>> factorySet = serverInjector.getInstance(
                Key.get(new TypeLiteral<Set<CallServiceChainFactory<Test2Service>>>() {
                }));
        final Set<CallServiceChainFactory<Test2Service>> factorySet2 = serverInjector.getInstance(
                Key.get(new TypeLiteral<Set<CallServiceChainFactory<Test2Service>>>() {
                }));
        assertEquals(factorySet.size(), 1);
        assertEquals(factorySet2.size(), 1);

        assertSame(factorySet.iterator().next(), factorySet2.iterator().next(), "CallServiceChainFactory er ikke en singleton som forventet");
    }

    /**
     * CallServiceChainFactory på klient skal være singleton og det skal være mulig å opprette en instans av klassen
     * uten noe aktivt ServiceRequestScope. Det skal også være mulig å opprette en ServiceChain siden klieter ikke
     * brunke ServiceRequestScope.
     */
    public void testCallServiceChainFactory_Client() {
        final Injector serverInjector = mock(Injector.class);
        final Injector clientInjector = createClientInjector(serverInjector, services);

        final Set<CallServiceChainFactory<Test2Service>> factorySet = clientInjector.getInstance(
                Key.get(new TypeLiteral<Set<CallServiceChainFactory<Test2Service>>>() {
                }));
        final Set<CallServiceChainFactory<Test2Service>> factorySet2 = clientInjector.getInstance(
                Key.get(new TypeLiteral<Set<CallServiceChainFactory<Test2Service>>>() {
                }));
        assertEquals(factorySet.size(), 1);
        assertEquals(factorySet2.size(), 1);
        assertSame(factorySet.iterator().next(), factorySet2.iterator().next(), "CallServiceChainFactory er ikke en singleton som forventet");
        assertNotNull(factorySet.iterator().next().createChain());
        assertNotSame(factorySet.iterator().next().createChain(), factorySet.iterator().next().createChain());
    }

    /**
     * Test kall til metode som ikke kalder videre og ikke bruker ServiceRequestScoped objekter i implementasjonen
     */
    public void testBasicSingleVmWireing_RequestScopeNotUsed() {
        final Injector serverInjector = createServerInjector(services);
        final Injector injector = createClientInjector(serverInjector, services);

        final Test1Service service1 = injector.getInstance(Test1Service.class);
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("henrik", "henrikPassword"));
        assertEquals(service1.helloWorld("test"), "Hello1: test");
        assertEquals(service1.helloWorld("test"), "Hello1: test");
    }

    /**
     * Test kall til metode som ikke kalder videre og ikke bruker ServiceRequestScoped objekter i implementasjonen
     */
    public void testBasicSingleVmWireing_RequestScopeUsed() {
        final Injector serverInjector = createServerInjector(services);
        final Injector injector = createClientInjector(serverInjector, services);

        final Test1Service service1 = injector.getInstance(Test1Service.class);
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("henrik", "henrikPassword"));
        final Test2Service service2 = injector.getInstance(Test2Service.class);
        assertEquals(service2.helloWorld("test"), "Hello2: test username: henrik");
        assertEquals(service2.helloWorld("test"), "Hello2: test username: henrik");
    }

    /**
     * Test kall til metode som kalder andre metoder. Ingen metoder krever tx
     */
    public void testCrossCallSingleVmWireing_NoEJBCallOnServer() {
        final Injector serverInjector = createServerInjectorWithTxAnnotation(services2);
        final Injector injector = createClientInjector(serverInjector, services2);

        final AService serviceA = injector.getInstance(AService.class);
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("henrik", "henrikPassword"));

        // Startende kall er ikke transaksjonelt
        assertEquals(serviceA.m1(new ArrayList<String>()), "[NoTx:AService.m1]");
        assertEquals(serviceA.m1(Arrays.asList("AService.m2")), "[NoTx:AService.m1 AService.m2]");
        assertEquals(serviceA.m1(Arrays.asList("AService.m2", "AService.m3")), "[NoTx:AService.m1 AService.m2 AService.m3]");
    }

    /**
     * Test kall til metode som ikke selv krever tx men som kaller andre metoder som krever det
     */
     public void testCrossCallSingleVmWireing_StartingCallHasNoTxOnMethodFollowingCallsMayHave() {
        final Injector serverInjector = createServerInjectorWithTxAnnotation(services2);
        final Injector injector = createClientInjector(serverInjector, services2);

        final AService serviceA= injector.getInstance(AService.class);
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("henrik", "henrikPassword"));

        assertEquals(serviceA.m1(Arrays.asList("BService.m2")), "[NoTx:AService.m1 [Tx:BService.m2]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "BService.m3")), "[NoTx:AService.m1 [Tx:BService.m2 [Tx:BService.m3]]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "BService.m3", "BService.m2")), "[NoTx:AService.m1 [Tx:BService.m2 [Tx:BService.m3 BService.m2]]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "BService.m3", "BService.m2", "CService.m1")), "[NoTx:AService.m1 [Tx:BService.m2 [Tx:BService.m3 BService.m2 CService.m1]]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "BService.m3", "BService.m1", "CService.m1")), "[NoTx:AService.m1 [Tx:BService.m2 [Tx:BService.m3 BService.m1 CService.m1]]]");

        assertEquals(serviceA.m1(Arrays.asList("BService.m2")), "[NoTx:AService.m1 [Tx:BService.m2]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "CService.m3")), "[NoTx:AService.m1 [Tx:BService.m2 CService.m3]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "CService.m3", "BService.m2")), "[NoTx:AService.m1 [Tx:BService.m2 CService.m3 BService.m2]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "CService.m3", "BService.m2", "CService.m1")), "[NoTx:AService.m1 [Tx:BService.m2 CService.m3 BService.m2 CService.m1]]");
        assertEquals(serviceA.m1(Arrays.asList("BService.m2", "CService.m1", "BService.m1", "CService.m1")), "[NoTx:AService.m1 [Tx:BService.m2 CService.m1 BService.m1 CService.m1]]");
    }

    /**
     * Test kall til metoder hvor startende kall har REQUIRES eller
     * REQUIRES_NEW transaction. Videre kall på server krever ikke transaksjoner
     */
    public void testCrossCallSingleVmWireing_StartingCallHasTxOnMethod() {
        final Injector serverInjector = createServerInjectorWithTxAnnotation(services2);
        final Injector injector = createClientInjector(serverInjector, services2);

        final BService serviceB = injector.getInstance(BService.class);
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("henrik", "henrikPassword"));

        // Startende kall har REQUIRES tx
        assertEquals(serviceB.m2(new ArrayList<String>()), "[Tx:BService.m2]");
        assertEquals(serviceB.m2(Arrays.asList("AService.m2")), "[Tx:BService.m2 AService.m2]");
        assertEquals(serviceB.m2(Arrays.asList("AService.m2", "AService.m3")), "[Tx:BService.m2 AService.m2 AService.m3]");

        // Startende kall har REQUIRES_NEW tx
        assertEquals(serviceB.m3(new ArrayList<String>()), "[Tx:BService.m3]");
        assertEquals(serviceB.m3(Arrays.asList("AService.m2")), "[Tx:BService.m3 AService.m2]");
        assertEquals(serviceB.m3(Arrays.asList("AService.m2", "AService.m3")), "[Tx:BService.m3 AService.m2 AService.m3]");

    }

    /**
     * Test kall til metoder hvor startende kall har REQUIRES eller
     * REQUIRES_NEW transaction. Videre kall på serveren krever også tx
     */
    public void testCrossCallSingleVmWireing_StartingCallHasNoTxOnMethodFollowingCallHas() {
        final Injector serverInjector = createServerInjectorWithTxAnnotation(services2);
        final Injector injector = createClientInjector(serverInjector, services2);

        final BService bService= injector.getInstance(BService.class);
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("henrik", "henrikPassword"));

        assertEquals(bService.m2(Arrays.asList("BService.m2")), "[Tx:BService.m2 BService.m2]");
        assertEquals(bService.m2(Arrays.asList("BService.m2", "BService.m3")), "[Tx:BService.m2 BService.m2 [Tx:BService.m3]]");
        assertEquals(bService.m2(Arrays.asList("BService.m2", "BService.m3", "BService.m2")), "[Tx:BService.m2 BService.m2 [Tx:BService.m3 BService.m2]]");
        assertEquals(bService.m2(Arrays.asList("BService.m2", "BService.m3", "BService.m2", "CService.m1")), "[Tx:BService.m2 BService.m2 [Tx:BService.m3 BService.m2 CService.m1]]");
        assertEquals(bService.m2(Arrays.asList("BService.m2", "BService.m3", "BService.m1", "CService.m1")), "[Tx:BService.m2 BService.m2 [Tx:BService.m3 BService.m1 CService.m1]]");

        assertEquals(bService.m3(Arrays.asList("BService.m2")), "[Tx:BService.m3 BService.m2]");
        assertEquals(bService.m3(Arrays.asList("BService.m2", "BService.m3")), "[Tx:BService.m3 BService.m2 [Tx:BService.m3]]");
        assertEquals(bService.m3(Arrays.asList("BService.m2", "BService.m3", "BService.m2")), "[Tx:BService.m3 BService.m2 [Tx:BService.m3 BService.m2]]");
        assertEquals(bService.m3(Arrays.asList("BService.m2", "BService.m3", "BService.m2", "CService.m1")), "[Tx:BService.m3 BService.m2 [Tx:BService.m3 BService.m2 CService.m1]]");
        assertEquals(bService.m3(Arrays.asList("BService.m2", "BService.m3", "BService.m1", "CService.m1")), "[Tx:BService.m3 BService.m2 [Tx:BService.m3 BService.m1 CService.m1]]");

    }

}

/**
 * En EJBServiceChainFactory  for test formål som legger på en ProxyHandler som endre på returverdien for metodekallet og legger
 * på informasjon om i hvilken context kallet ble uført. Denne factory'en kan kun brukte på metoder som
 * returnerer {@code String}
 *
 * @param <S>
 */
class AnnotatingEjbServiceChainFactory<S> implements EJBServiceChainFactory<S> {
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;

    @Inject
    public AnnotatingEjbServiceChainFactory(Provider<ServiceRequestContext> serviceRequestContextProvider) {
        this.serviceRequestContextProvider = serviceRequestContextProvider;
    }

    @Override
    public float getChainPosition() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProxyHandler<S> createChain() {
        return null;
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        ChainedProxyHandler<S> h = new ChainedProxyHandler<S>() {
            @Override
            protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {

                String result = SkifUtil.cast(String.class, chained.invoke(proxy, method, args));
                if (serviceRequestContext.isContinuation()) {
                    if (serviceRequestContext.isTransactional()) {
                        result = "w:" + result;
                    } else {
                        result = "r:" + result;
                    }
                } else {
                    if (serviceRequestContext.isTransactional()) {
                        result = "[Tx:" + result + "]";
                    } else {
                        result = "[NoTx:" + result + "]";
                    }
                }
                return result;
            }
        };
        h.setChained(firstInChain);
        return h;
    }
}
