package no.statkart.skif.skiftest.wsapi.mapping;


import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.AList;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class SkifTestMappingW2DTest {
    SkifTestMapper<?> mapper = new SkifTestMapper();
    SkifTestMapping map = mapper.getMapping();

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
        no.statkart.skif.skiftest.wsapi.domain.A source = new no.statkart.skif.skiftest.wsapi.domain.A();
        source.setText("a");
        A target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target.getText(), "a");
    }

    public void testMapB() {
        no.statkart.skif.skiftest.wsapi.domain.B source = new no.statkart.skif.skiftest.wsapi.domain.B();
        source.setText("b");
        B target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target.getText(), "b");
    }


    public void testMapAList() {
        AList source = new AList();
        Set<A> target;

        no.statkart.skif.skiftest.wsapi.domain.A a1 = new no.statkart.skif.skiftest.wsapi.domain.A();
        a1.setText("a1");
        no.statkart.skif.skiftest.wsapi.domain.A a2 = new no.statkart.skif.skiftest.wsapi.domain.A();
        a2.setText("a2");
        source.getItem().add(a1);
        source.getItem().add(a2);
        //noinspection unchecked
        target = map.w2d(source, Set.class);
        assertEquals(target.size(), 2);
        assertEquals(target.iterator().next().getClass(), no.statkart.skif.skiftest.domain.A.class);
    }
}
