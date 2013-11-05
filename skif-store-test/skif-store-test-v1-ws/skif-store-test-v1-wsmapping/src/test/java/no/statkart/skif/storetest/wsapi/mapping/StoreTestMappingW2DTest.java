package no.statkart.skif.storetest.wsapi.mapping;


import junit.framework.TestCase;
import no.statkart.skif.storetest.domain.basic.Simple;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestMappingW2DTest extends TestCase {
    private StoreTestMappingTestContext testContext;

    @BeforeMethod
    public void setUpTestCase() {
        testContext = new StoreTestMappingTestContext();
    }

    @Test
    public void testMapString() {
        final StoreTestMapping map = testContext.buildMapping();

        String source = "test";
        String target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, "test");
    }

    @Test
    public void testMapIntger() {
        final StoreTestMapping map = testContext.buildMapping();

        Integer source = 5;
        Integer target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, new Integer(5));
    }

    @Test
    public void testMapInt() {
        final StoreTestMapping map = testContext.buildMapping();

        int source = 5;
        int target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, 5);
    }


    @Test
    public void testMapSimple() {
        final StoreTestMapping map = testContext.buildMapping();

        no.statkart.skif.storetest.wsapi.domain.basic.Simple source = new no.statkart.skif.storetest.wsapi.domain.basic.Simple();
        no.statkart.skif.storetest.wsapi.domain.basic.SimpleId sourceId = new no.statkart.skif.storetest.wsapi.domain.basic.SimpleId();
        sourceId.setValue("10");
        source.setId(sourceId);
        source.setText("Test");
        Simple target = map.w2d(source, Simple.class);
        assertEquals(target.getId().getValue(), new Long(10));
        assertEquals(target.getText(), "Test");
    }
}


