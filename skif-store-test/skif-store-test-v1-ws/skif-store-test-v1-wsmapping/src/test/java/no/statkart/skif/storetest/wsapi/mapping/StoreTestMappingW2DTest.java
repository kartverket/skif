package no.statkart.skif.storetest.wsapi.mapping;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.wsapi.mapping.testutils.StoreTestMappingTestContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testMapInteger() {
        final StoreTestMapping map = testContext.buildMapping();

        Integer source = 5;
        Integer target = map.w2d(source);
        assertThat(target).isEqualTo(Integer.valueOf(5));
    }

    @Test
    public void testMapInt() {
        final StoreTestMapping map = testContext.buildMapping();

        int source = 5;
        int target = map.w2d(source);
        assertThat(target).isEqualTo(5);
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
        assertThat(target.getId().getValue()).isEqualTo(10L);
        Assert.assertEquals(target.getText(), "Test");
    }

    @Test
    public void testMapStoreTestBubbleIdList_complexType() throws NoSuchMethodException {
        final StoreTestMapping map = testContext.buildMapping();

        no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList list = new no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList();
        no.statkart.skif.storetest.wsapi.domain.basic.SimpleId sourceId = new no.statkart.skif.storetest.wsapi.domain.basic.SimpleId();
        sourceId.setValue("10");
        list.getItem().add(sourceId);

        Method getVersionsForList = StoreService.class.getMethod("getVersionsForList", Collection.class, SnapshotVersion.class, SnapshotVersion.class);

        Object result = map.w2d(list, list.getClass(), getVersionsForList.getGenericParameterTypes()[0]);

        Assert.assertEquals(result.getClass(), ArrayList.class);
    }
}


