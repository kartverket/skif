package no.statkart.skif.skiftest.service.test;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.skiftest.config.SkifTestGroupABCDServices;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test(groups = "server-required")
public class TxTest {
    private Injector injector = null;
    //private ServiceMode mode = ServiceMode.JEE;


    @DataProvider(name = "serverModes")
    public Object[][] createServerModes() {
        return new Object[][]{
                {ServiceMode.JEE},
                {ServiceMode.SINGLE_VM},
        };
    }

    @DataProvider(name = "serverModesJEE")
    public Object[][] createServerModesJEE() {
        return new Object[][]{
                {ServiceMode.JEE},
        };
    }

    @DataProvider(name = "serverModesSVM")
    public Object[][] createServerModesSVM() {
        return new Object[][]{
                {ServiceMode.SINGLE_VM},
        };
    }
    private void setlogin() {
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("frehen", "matrikkel2"));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set("https://localhost:7002");
    }

    public Injector createClientInjector(ServiceMode mode) {
        ModuleConfiguration cfg = null;
        return new ModuleBuilder()
                .setModuleClass(ClientModule.class)
                .setSingleVmServerModuleClass(SkifTestServerModule.class)
                .setServiceMode(mode)
                .buildInjector();
    }

    public static class ClientModule extends SkifModule {

        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupABCDServices().getServices(), new SkifTestMapper().getMapping()));
        }
    }

    /**
     * Test kall til metode som kalder andre metoder. Ingen metoder krever tx
     */
    @Test(dataProvider = "serverModes")
    public void testCrossCallSingleVmWireing_NoEJBCallOnServer(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();
        final AService serviceA = injector.getInstance(AService.class);

        // Startende kall er ikke transaksjonelt
        assertEquals(serviceA.m1(new ArrayList<String>()), "[NoTx:AService.m1]");
        assertEquals(serviceA.m1(Arrays.asList("AService.m2")), "[NoTx:AService.m1 AService.m2]");
        assertEquals(serviceA.m1(Arrays.asList("AService.m2", "AService.m3")), "[NoTx:AService.m1 AService.m2 AService.m3]");
    }


    /**
     * Test kall til metode som ikke selv krever tx men som kaller andre metoder som krever det
     */
    @Test(dataProvider = "serverModes")
    public void testCrossCallSingleVmWireing_StartingCallHasNoTxOnMethodFollowingCallsMayHave(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();

        final AService serviceA = injector.getInstance(AService.class);

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
    @Test(dataProvider = "serverModes")
    public void testCrossCallSingleVmWireing_StartingCallHasTxOnMethod(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();

        final BService serviceB = injector.getInstance(BService.class);

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
    @Test(dataProvider = "serverModes")
    public void testCrossCallSingleVmWireing_StartingCallHasNoTxOnMethodFollowingCallHas(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();

        final BService bService = injector.getInstance(BService.class);

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

