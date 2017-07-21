package no.statkart.skif.storetest.wsapi.mapping;


import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.wsapi.mapping.testutils.StoreTestMappingTestContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestMappingW2DTest {
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
        Assert.assertNotNull(target);
        Assert.assertEquals(target, "test");
    }

    @Test
    public void testMapIntger() {
        final StoreTestMapping map = testContext.buildMapping();

        Integer source = 5;
        Integer target = map.w2d(source);
        Assert.assertNotNull(target);
        Assert.assertEquals(target, new Integer(5));
    }

    @Test
    public void testMapInt() {
        final StoreTestMapping map = testContext.buildMapping();

        int source = 5;
        Integer target = map.w2d(source);
        Assert.assertNotNull(target);
        Assert.assertEquals(target.intValue(), 5);
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
        Assert.assertEquals(target.getId().getValue(), new Long(10));
        Assert.assertEquals(target.getText(), "Test");
    }
}


