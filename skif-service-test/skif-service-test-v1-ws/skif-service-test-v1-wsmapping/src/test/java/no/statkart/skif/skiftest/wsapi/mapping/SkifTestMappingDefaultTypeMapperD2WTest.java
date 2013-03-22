package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.domain.C;
import no.statkart.skif.skiftest.domain.M;
import no.statkart.skif.skiftest.wsapi.domain.AList;
import no.statkart.skif.skiftest.wsapi.domain.AMap;
import org.testng.annotations.Test;

import java.util.*;

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

//    /**
//     * Tester for å belyse 	SKIF-160
//     */
//    @Test(enabled = false)
//    public void testMapTestC(){
//        C source = new C();
//        source.setAs(new A[]{new A("a1"), new A("a2")});
//        source.setInts(new int[]{1,2,3,4});
//        source.setStrings(new String[]{"s1", "s2", "s3"});
//
//        no.statkart.skif.skiftest.wsapi.domain.C target = map.d2w(source);
//        assertEquals(source.getInts(), target.getInts());
//        assertEquals(source.getStrings(), target.getStrings());
//        for(int i = 0; i < source.getAs().length;i++) {
//            assertEquals(source.getAs()[i].getText(), target.getAs().toArray(new A[]{})[i].getText());
//        }
//    }

    public void testMapMap() {
        HashMap<String, Set<A>> aMap = new HashMap<String, Set<A>>();
        aMap.put("Foo", Collections.singleton(new A("Bar")));
        M source = new M();
        source.setMapOfA(aMap);

        no.statkart.skif.skiftest.wsapi.domain.M target = map.d2w(source);

        AMap mapOfA = target.getMapOfAs();
        List<AMap.Entry> entries = mapOfA.getEntry();
        assertEquals(entries.size(), 1);
        assertEquals(entries.get(0).getKey(), "Foo");
        AList aList = entries.get(0).getValue();
        assertEquals(aList.getItem().size(), 1);
        assertEquals(aList.getItem().get(0).getText(), "Bar");
    }
}
