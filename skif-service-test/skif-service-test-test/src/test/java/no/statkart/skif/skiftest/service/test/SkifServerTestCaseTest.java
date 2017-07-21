package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import no.statkart.skif.util.testsupport.TestTransactionAttribute;
import no.statkart.skif.util.testsupport.TestTransactionAttributeType;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class SkifServerTestCaseTest extends SkifServerTestCase {
    @Inject
    AService serviceA;
    @Inject
    ServiceRequestContext serviceRequestContext;

    public SkifServerTestCaseTest() {
        super(SkifTestServerModule.class);
    }

    @TestTransactionAttribute(TestTransactionAttributeType.TX_BEAN)
    public void testRunOnServerUsingBeanManagedTransaction() {
        String result = serviceA.m1(Collections.singletonList("BService.m2"));
        assertTrue(serviceRequestContext.isBeanManagedTransaction());
        assertTrue(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 BService.m2");
    }

    @TestTransactionAttribute(TestTransactionAttributeType.TX_NOT_SUPPORTED)
    public void testRunOnServerUsingTxNotSupported() {
        String result = serviceA.m1(Collections.singletonList("BService.m2"));
        assertTrue(serviceRequestContext.isContainerManagedTransaction());
        assertFalse(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 [Tx:BService.m2]");
    }

    @TestTransactionAttribute(TestTransactionAttributeType.TX_REQUIRES_NEW)
    public void testRunOnServerUsingTxRequiresNew() {
        String result = serviceA.m1(Collections.singletonList("BService.m2"));
        assertTrue(serviceRequestContext.isContainerManagedTransaction());
        assertTrue(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 BService.m2");
    }

    @TestTransactionAttribute(TestTransactionAttributeType.TX_REQUIRED)
    public void testRunOnServerUsingTxRequired() {
        String result = serviceA.m1(Collections.singletonList("BService.m2"));
        assertTrue(serviceRequestContext.isContainerManagedTransaction());
        assertTrue(serviceRequestContext.inTx());
        assertEquals(result, "AService.m1 BService.m2");
    }

    /**
     * Skal gi samme resultat som hvis @TestTransactionAttribute(TestTransactionAttributeType.TX_REQUIRED) er angitt
     */
    @TestTransactionAttribute
    public void testRunOnServerUsingDefaultTestTransactionAttributeValue() {
        testRunOnServerUsingTxRequired();
    }

    /**
     * Skal gi samme resultat som hvis @TestTransactionAttribute(TestTransactionAttributeType.TX_REQUIRED) er angitt
     */
    public void testRunOnServerUsingNoTestTransactionAttribute() {
        testRunOnServerUsingTxRequired();
    }
}
