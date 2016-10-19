package no.statkart.skif.skiftest.service.test;

import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class TxTest extends SkifTestCase {

    public TxTest() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    /**
     * Test kall til metode som kalder andre metoder. Ingen metoder krever tx
     */
    @Test
    public void testCrossCall_NoEJBCallOnServer() {
        final AService serviceA = injector.getInstance(AService.class);

        // Startende kall er ikke transaksjonelt
        assertEquals(serviceA.m1(new ArrayList<String>()), "[NoTx:AService.m1]");
        assertEquals(serviceA.m1(Arrays.asList("AService.m2")), "[NoTx:AService.m1 AService.m2]");
        assertEquals(serviceA.m1(Arrays.asList("AService.m2", "AService.m3")), "[NoTx:AService.m1 AService.m2 AService.m3]");
    }


    /**
     * Test kall til metode som ikke selv krever tx men som kaller andre metoder som krever det
     */
    @Test
    public void testCrossCall_StartingCallHasNoTxOnMethodFollowingCallsMayHave() {
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
    @Test
    public void testCrossCall_StartingCallHasTxOnMethod() {
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
    @Test
    public void testCrossCall_StartingCallHasNoTxOnMethodFollowingCallHas() {
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

