package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 */
@Test
public class SkifServerTestCaseTest extends SkifServerTestCase {
    public SkifServerTestCaseTest() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    public void testRunOnServerUsingBeanManagedTransaction() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            AService serviceA;
            @Inject
            ServiceRequestContext serviceRequestContext;

            public Object run() {
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

    public void testRunOnServerUsingTxSupported() {
        server.runInTxSupported(new RunOnServerMethod() {
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
