package no.statkart.skif.storetest.wsapi.mapping;


import junit.framework.TestCase;
import no.statkart.skif.storetest.domain.A;
import no.statkart.skif.storetest.domain.B;
import no.statkart.skif.storetest.wsapi.domain.AList;

import java.util.HashSet;
import java.util.Set;

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

    public void testMapA() {
        no.statkart.skif.storetest.wsapi.domain.A source = new no.statkart.skif.storetest.wsapi.domain.A();
        source.setText("a");
        A target = map.w2d(source);
        assertNotNull(target);
        assertEquals("a", target.getText());
    }

    public void testMapB() {
        no.statkart.skif.storetest.wsapi.domain.B source = new no.statkart.skif.storetest.wsapi.domain.B();
        source.setText("b");
        B target = map.w2d(source);
        assertNotNull(target);
        assertEquals("b", target.getText());
    }


    public void testMapAList() {
        AList source = new AList();
        Set<A> target = new HashSet<A>();

        no.statkart.skif.storetest.wsapi.domain.A a1 = new no.statkart.skif.storetest.wsapi.domain.A();
        a1.setText("a1");
        no.statkart.skif.storetest.wsapi.domain.A a2 = new no.statkart.skif.storetest.wsapi.domain.A();
        a2.setText("a2");
        source.getItem().add(a1);
        source.getItem().add(a2);
        target = map.w2d(source, target);
        assertEquals(2, target.size());
        assertEquals(target.iterator().next().getClass(), no.statkart.skif.storetest.domain.A.class);
    }
}
