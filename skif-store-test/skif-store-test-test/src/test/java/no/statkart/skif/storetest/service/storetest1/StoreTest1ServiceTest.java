package no.statkart.skif.storetest.service.storetest1;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.exception.ImplementationException;
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
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.wsapi.exception.impl.mapping.StoreTestExceptionMapper;
import no.statkart.skif.util.NullHostnameVerifier;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.net.ssl.HostnameVerifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class StoreTest1ServiceTest extends StoreTestTestCase {


    /**
     * Test kall til metode som kalder andre metoder. Ingen metoder krever tx
     */
    public void testStoreTest1Service() {
        final StoreTest1Service storeTest1Service = injector.getInstance(StoreTest1Service.class);

        storeTest1Service.clear();
        assertEquals(storeTest1Service.put("key1", "value1"), null);
        assertEquals(storeTest1Service.get("key1"), "value1");
        try {
            storeTest1Service.putThatFails("key1", "value2");
            fail("Forventet exception");
        } catch (ImplementationException t) {
            System.out.println(t);
        }
        assertEquals(storeTest1Service.get("key1"), "value1", "Forrige metode skulle ikke ha endret 'key1'");
        assertEquals(storeTest1Service.remove("key1"), "value1");
        assertEquals(storeTest1Service.get("key1"), null);
        System.out.println("Done");
    }


     @Test(invocationCount = 1 /*200*/)
    public void testStoreTest1ServiceMultipleThreads() {
         testStoreTest1Service();
    }


}
