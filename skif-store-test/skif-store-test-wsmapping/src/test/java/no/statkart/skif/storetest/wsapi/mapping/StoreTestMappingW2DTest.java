package no.statkart.skif.storetest.wsapi.mapping;


import junit.framework.TestCase;
import no.statkart.skif.storetest.domain.TestBubble;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestMappingW2DTest extends TestCase {
    StoreTestMapper mapper = new StoreTestMapper();
    StoreTestMapping map = mapper.getMapping();

    public void testMapString() {
        String source = "test";
        String target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, "test");
    }

    public void testMapIntger() {
        Integer source = 5;
        Integer target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, new Integer(5));
    }

    public void testMapInt() {
        int source = 5;
        int target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, 5);
    }


    public void testMapTestBubble() {
        no.statkart.skif.storetest.wsapi.domain.TestBubble source = new no.statkart.skif.storetest.wsapi.domain.TestBubble();
        no.statkart.skif.storetest.wsapi.domain.TestBubbleId sourceId = new no.statkart.skif.storetest.wsapi.domain.TestBubbleId();
        sourceId.setValue("10");
        source.setId(sourceId);
        source.setText("Test");
        TestBubble target = map.w2d(source);
        assertEquals(target.getId().getValue(), 10L);
        assertEquals(target.getText(), "Test");
    }
}


