package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.util.testsupport.SkifMixedTestCase;
import no.statkart.skif.util.testsupport.SkifServer3TestCase;
import no.statkart.skif.util.testsupport.TestServerMethodTransactionAttribute;
import no.statkart.skif.util.testsupport.TestServerMethodTransactionAttributeType;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class SkifServer3TestCaseTest extends SkifServer3TestCase {
    @Inject
    AService serviceA;
    @Inject
    ServiceRequestContext serviceRequestContext;

    public SkifServer3TestCaseTest() {
        super(SkifTestServerModule.class);
    }

    @TestServerMethodTransactionAttribute(TestServerMethodTransactionAttributeType.BEAN)
    public void testRunOnServerUsingBeanManagedTransaction() {
        String result = serviceA.m1(Arrays.asList("BService.m2"));
        assertTrue(serviceRequestContext.isBeanManagedTransaction());
        assertTrue(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 BService.m2");
    }

    @TestServerMethodTransactionAttribute(TestServerMethodTransactionAttributeType.NOT_SUPPORTED)
    public void testRunOnServerUsingTxNotSupported() {
        String result = serviceA.m1(Arrays.asList("BService.m2"));
        assertTrue(serviceRequestContext.isContainerManagedTransaction());
        assertFalse(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 [Tx:BService.m2]");
    }

    @TestServerMethodTransactionAttribute(TestServerMethodTransactionAttributeType.REQUIRES_NEW)
    public void testRunOnServerUsingTxRequiresNew() {
        String result = serviceA.m1(Arrays.asList("BService.m2"));
        assertTrue(serviceRequestContext.isContainerManagedTransaction());
        assertTrue(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 BService.m2");
    }

}
