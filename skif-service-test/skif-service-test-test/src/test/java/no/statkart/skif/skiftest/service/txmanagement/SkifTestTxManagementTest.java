package no.statkart.skif.skiftest.service.txmanagement;

import no.statkart.skif.exception.ValidationException;
import no.statkart.skif.skiftest.config.SkifTestTxManagementServerModule;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTCascadeService;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAService;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class SkifTestTxManagementTest extends SkifTestCase {

    public SkifTestTxManagementTest() {
        setModuleClass(SkifTestTxManagementClientModule.class);
        setSingleVmServerModuleClass(SkifTestTxManagementServerModule.class);
    }


    /**
     * Test bean managed transactions. Klient kalder BMT ejb som ikke kaller videre
     */
    @Test
    public void testBeanManagedTx() {
        final BeanManagedTxAService bmtServiceA = injector.getInstance(BeanManagedTxAService.class);

        bmtServiceA.clear();
        assertNull(bmtServiceA.get("key1"));
        bmtServiceA.put("key1", "value1");
        assertEquals(bmtServiceA.put("key1", "anotherValue1"), "value1");
        bmtServiceA.multiPut("key1", "multiValue1", "key2", "multiValue2");
        assertEquals(bmtServiceA.get("key1"), "multiValue1");
        assertEquals(bmtServiceA.get("key2"), "multiValue2");
        try {
            // key3 skal bli satt selv om metoden kaster exception. Dette fordi metoden bruker BMT og gjør commit for hver put
            bmtServiceA.multiPut("key3", "multiValue3", null, "XXX");
            fail("Forventet ValidationException");
        } catch (ValidationException e) {
            // OK, forventet
        }
        assertEquals(bmtServiceA.get("key3"), "multiValue3");
    }

    @Test
    public void test() {
        final BeanManagedTxAService bmtServiceA = injector.getInstance(BeanManagedTxAService.class);
        bmtServiceA.clear();
        bmtServiceA.put("key1", "value1");

        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);
        for (int i = 0; i < 100; i++) {
            try {
                // key3 skal ikke bli satt, da metoden kaster exception og bruker CMT {@code TransactionAttributeType.REQUIRED}
                cascadeService.containerTest3("key1", "multiValue1", null, "multiValue2");
                fail("Forventet ValidationException");
            } catch (Throwable t) {
                assertThat(t).describedAs("forventet exception").isInstanceOf(ValidationException.class);
            }
        }
        assertEquals(bmtServiceA.get("key1"), "value1");

    }

    /**
     * Test container managed transactions. Klient kalder CMT ejb som ikke kaller videre
     */
    @Test
    public void testContainerManagedTx() {
        final ContainerManagedTxAService cmtServiceA = injector.getInstance(ContainerManagedTxAService.class);

        cmtServiceA.clear();
        assertNull(cmtServiceA.get("key1"));
        assertNull(cmtServiceA.put("key1", "value1"));
        assertEquals(cmtServiceA.put("key1", "anotherValue1"), "value1");
        cmtServiceA.multiPut("key1", "multiValue1", "key2", "multiValue2");
        assertEquals(cmtServiceA.get("key1"), "multiValue1");
        assertEquals(cmtServiceA.get("key2"), "multiValue2");
        try {
            // key3 skal ikke bli satt, da metoden kaster exception og bruker CMT {@code TransactionAttributeType.REQUIRED}
            cmtServiceA.multiPut("key3", "multiValue3", null, "XXX");
            fail("Forventet ValidationException");
        } catch (ValidationException e) {
            // Ok, forventet

        }
        assertEquals(cmtServiceA.get("key3"), null);
    }

    /**
     * Test container managed transactions. Klient kalder CMT ejb som for noen av metodene kaller videre på annen CMT ejb.
     * Metodene put og get kaller ikke videre. Denne testen verifiserer at basis put og get virker.
     */
    @Test
    public void testCascadedContainerManagedTx_getAndPut() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        assertNull(cascadeService.get("key1"));
        assertNull(cascadeService.put("key1", "valueX"));
        assertEquals(cascadeService.get("key1"), "valueX");
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Metoden containerTest1 anvender TransactionAttributeType.REQUIRED.
     */
    @Test
    public void testCascadedContainerManagedTxTest1_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.containerTest1("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Siden kallet til den andre ejb feiler skal ingen av de to endringer commites.
     */
    @Test(enabled = false)
    public void testCascadedContainerManagedTxTest1_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.containerTest1("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertNull(cascadeService.get("key1"));
            assertNull(cascadeService.get("key2"));
        }
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Metoden containerTest1 anvender TransactionAttributeType.SUPPORTS.
     */
    @Test
    public void testCascadedContainerManagedTxTest2_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.put("key1", "value1");
        cascadeService.put("key2", "value2");
        cascadeService.containerTest1("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }


    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Siden containerTest2 metoden kun har SUPPORTS og kall nr 2 til den andre ejb feiler, skal kun den første endring committes.
     */
    @Test
    public void testCascadedContainerManagedTxTest2_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.containerTest2("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertEquals(cascadeService.get("key1"), "value1");
            assertNull(cascadeService.get("key2"));
        }
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Metoden containerTest3 anvender TransactionAttributeType.REQUIRED.
     */
    @Test
    public void testCascadedContainerManagedTxTest3_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.containerTest3("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Siden containerTest3 metoden kun har REQUIRED og kall nr 2 til den andre ejb feiler, skal ingen endringer committes.
     */
    @Test
    public void testCascadedContainerManagedTxTest3_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.containerTest3("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertNull(cascadeService.get("key1"));
            assertNull(cascadeService.get("key2"));
        }
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Metoden containerTest4 anvender default transaction attribute, hvilket er det samme som TransactionAttributeType.REQUIRED.
     */
    @Test
    public void testCascadedContainerManagedTxTest4_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.containerTest4("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen CMT ejb.
     * Metoden containerTest4 anvender default transaction type. Siden kall nr 2 til den andre ejb feiler, skal ingen av endringene committes.
     */
    @Test
    public void testCascadedContainerManagedTxTest4_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.containerTest4("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertNull(cascadeService.get("key1"));
            assertNull(cascadeService.get("key2"));
        }
    }


    /////////////////////////// Container Managed Transaction kaller Bean Managed Transaction /////////////////

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen BMT ejb.
     * Metoden containerTest1 anvender TransactionAttributeType.REQUIRED.
     */
    @Test
    public void testCascadedBeanManagedTxTest1_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.beanTest1("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen BMT ejb.
     * Siden kallet til den andre ejb feiler skal ingen av de to endringer commites.
     */
    @Test
    public void testCascadedBeanManagedTxTest1_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.beanTest1("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertNull(cascadeService.get("key1"));
            assertNull(cascadeService.get("key2"));
        }
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen BMT ejb.
     * Metoden containerTest1 anvender TransactionAttributeType.SUPPORTS.
     */
    @Test
    public void testCascadedBeanManagedTxTest2_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.beanTest1("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }


    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen BMT ejb.
     * Siden containerTest2 metoden kun har SUPPORTS og kall nr 2 til den andre ejb feiler, skal kun den første endring committes.
     */
    @Test
    public void testCascadedBeanManagedTxTest2_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.beanTest2("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertEquals(cascadeService.get("key1"), "value1");
            assertNull(cascadeService.get("key2"));
        }
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen BMT ejb.
     * Metoden containerTest1 anvender TransactionAttributeType.REQUIRED.
     */
    @Test
    public void testCascadedBeanManagedTxTest3_cascadedPut_ok() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        cascadeService.beanTest1("key1", "value1", "key2", "value2");
        assertEquals(cascadeService.get("key1"), "value1");
        assertEquals(cascadeService.get("key2"), "value2");
    }

    /**
     * Tester container managed transactions. Klient kalder CMT ejb som kaller videre på annen BMT ejb.
     * Metoden beanTest3 har REQUIRES og kall nr 2 til den andre ejb feiler, men første kall har allerede blitt committet
     * siden ejben det blir kallt på bruker bean managed transactions og kjører alle kall i egen transaksjon.
     */
    @Test
    public void testCascadedBeanManagedTxTest3_cascadedPut_rollback() {
        final ContainerManagedTxCMTCascadeService cascadeService = injector.getInstance(ContainerManagedTxCMTCascadeService.class);

        cascadeService.clear();
        try {
            cascadeService.beanTest3("key1", "value1", null, "value2");
            fail("Forventet exception");
        } catch (ValidationException e) {
            assertEquals(cascadeService.get("key1"), "value1");
            assertNull(cascadeService.get("key2"));
        }
    }
}

