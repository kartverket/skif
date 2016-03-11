package no.statkart.skif.storetest.service.storetest1;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test av StoreTest1Service
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class StoreTest1ServiceTest extends StoreTestTestCase {

    /**
     * Test kall til metode som kalder andre metoder. Ingen metoder krever tx
     */
    @Test
    public void testStoreTest1Service() {
        final StoreTest1Service storeTest1Service = injector.getInstance(StoreTest1Service.class);

        storeTest1Service.clear();
        assertEquals(storeTest1Service.put("key1", "value1"), null);
        assertEquals(storeTest1Service.get("key1"), "value1");
        try {
            storeTest1Service.putThatFails("key1", "value2");
            fail("Forventet exception");
        } catch (Throwable t) {
            assertThat(t).describedAs("forventet exception").isInstanceOf(ImplementationException.class);
        }
        assertEquals(storeTest1Service.get("key1"), "value1", "Forrige metode skulle ikke ha endret 'key1'");
        assertEquals(storeTest1Service.remove("key1"), "value1");
        assertEquals(storeTest1Service.get("key1"), null);
    }


    @Test(invocationCount = 200, groups = "slow")
    public void testStoreTest1ServiceMultipleThreads() {
        testStoreTest1Service();
    }


}
