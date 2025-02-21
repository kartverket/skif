package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.util.testsupport.SkifMixedTestCase;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 */
@Test(groups = "singlevm-required")
public class SkifMixedTestCaseTest extends SkifMixedTestCase {

    public SkifMixedTestCaseTest() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    public void testRunOnServerUsingBeanManagedTransaction() {
        // Her er vi på klienten og injector peker på klientmodulen
        String s = injector.getInstance(Key.get(String.class, Names.named("client")));
        assertEquals(s, "ClientString");

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            AService serviceA;
            @Inject
            ServiceRequestContext serviceRequestContext;

            public Object run() {
                // Her er vi på server
                String result = serviceA.m1(Arrays.asList("BService.m2"));
                assertTrue(serviceRequestContext.isBeanManagedTransaction());
                assertTrue(serviceRequestContext.inTx());
                assertEquals(result, "AService.m1 BService.m2");

                return result;
            }
        });
    }

    public void testRunOnServerUsingTxNotSupported() {
        server.runInTxNotSupported(new RunOnServerMethod() {
            @Inject
            AService serviceA;
            @Inject
            ServiceRequestContext serviceRequestContext;

            public Object run() {
                String result = serviceA.m1(Arrays.asList("BService.m2"));
                assertTrue(serviceRequestContext.isContainerManagedTransaction());
                assertFalse(serviceRequestContext.inTx());
                assertEquals(result, "AService.m1 [Tx:BService.m2]");

                return result;
            }
        });
    }

    public void testRunOnServerUsingTxRequiresNew() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            AService serviceA;
            @Inject
            ServiceRequestContext serviceRequestContext;

            public Object run() {
                String result = serviceA.m1(Arrays.asList("BService.m2"));
                assertTrue(serviceRequestContext.isContainerManagedTransaction());
                assertTrue(serviceRequestContext.inTx());
                assertEquals(result, "AService.m1 BService.m2");

                return result;
            }
        })  ;
    }

}
