package no.statkart.skif.storetest.wsapi.mapping;


import junit.framework.TestCase;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
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


    public void testMapSimple() {
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


