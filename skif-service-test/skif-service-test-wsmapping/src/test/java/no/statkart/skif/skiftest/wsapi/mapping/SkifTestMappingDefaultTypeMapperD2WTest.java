package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.AList;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 */
@Test
public class SkifTestMappingDefaultTypeMapperD2WTest {
    SkifDefaultTypeMapperTestMapper configuration = new SkifDefaultTypeMapperTestMapper();
    SkifTestMapping map = configuration.getMapping();

    public void testMapString() {
        String source = "test";
        String target = map.d2w(source);
        assertNotNull(target);
        assertEquals(target, "test");
    }

    public void testMapInteger() {
        Integer source = 5;
        Integer target = map.d2w(source);
        assertNotNull(target);
        assertEquals(target, new Integer(5));
    }

    public void testMapInt() {
        int source = 5;
        int target = map.d2w(source);
        assertNotNull(target);
        assertEquals(target, 5);

        target = map.d2w(source, int.class);
        assertNotNull(target);
        assertEquals(target, 5);

    }

    /**
     * Tester mapping2 av et API TestBubbleId objekt til Web Service TestBubbelId objekt
     */
    public void testMapTestA() {
        A source = new A("10");
        no.statkart.skif.skiftest.wsapi.domain.A target = map.d2w(source);
        assertNotNull(target);
        assertEquals(target.getText(), "10");


        target = map.d2w(source, no.statkart.skif.skiftest.wsapi.domain.A.class);
        assertNotNull(target);
        assertEquals(target.getText(), "10");
    }

    public void testMapTestB() {
        B source = new B("10");
        no.statkart.skif.skiftest.wsapi.domain.B target = map.d2w(source);
        assertNotNull(target);
        assertEquals(target.getText(), "10");


        target = map.d2w(source, no.statkart.skif.skiftest.wsapi.domain.B.class);
        assertNotNull(target);
        assertEquals(target.getText(), "10");
    }

    /**
     * Tester mapping2 av et sett med API TestBubbleId objekter til en liste Web Service TestBubbleId objekter
     */
    public void testMapTestASet() {
        Set<A> source = new HashSet<A>();
        AList target = new AList();
        A a1 = new A("a1");
        A a2 = new A("a2");
        source.add(a1);
        source.add(a2);
        target = map.d2w(source, target);
        assertEquals(target.getItem().size(), 2);
        assertEquals(target.getItem().iterator().next().getClass(), no.statkart.skif.skiftest.wsapi.domain.A.class);

        target = map.d2w(source, AList.class);
        assertEquals(target.getItem().size(), 2);
        assertEquals(target.getItem().iterator().next().getClass(), no.statkart.skif.skiftest.wsapi.domain.A.class);
    }
}
