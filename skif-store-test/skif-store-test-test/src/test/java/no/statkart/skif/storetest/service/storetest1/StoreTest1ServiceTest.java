package no.statkart.skif.storetest.service.storetest1;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.chain.ServiceChainFactories;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.D2WAdapterProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.util.NullHostnameVerifier;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.net.ssl.HostnameVerifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test(groups = "server-required")
public class StoreTest1ServiceTest {
    private Injector injector = null;

    @DataProvider(name = "serverModes")
    public Object[][] createServerModes() {
//        return createServerModesAll();
//        return createServerModesJEE();
        return createServerModesSVM();
    }

    @DataProvider(name = "serverModesAll")
    public Object[][] createServerModesAll() {
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
                .setSingleVmServerModuleClass(StoreTestServerModule.class)
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
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices(), new IdentityMapper().getMapping()));
        }
    }

    /**
     * Test kall til metode som kalder andre metoder. Ingen metoder krever tx
     */
    @Test(dataProvider = "serverModes")
    public void testStoreTest1Service(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();
        final StoreTest1Service storeTest1Service = injector.getInstance(StoreTest1Service.class);

        storeTest1Service.clear();
        assertEquals(storeTest1Service.put("key1", "value1"), null);
        assertEquals(storeTest1Service.get("key1"), "value1");
        assertEquals(storeTest1Service.remove("key1"), "value1");
        assertEquals(storeTest1Service.get("key1"), null);
    }
}
