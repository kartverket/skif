package no.statkart.skif.skiftest.wsapi.mapping;


import com.google.common.primitives.Ints;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.domain.M;
import no.statkart.skif.skiftest.wsapi.domain.AList;
import no.statkart.skif.skiftest.wsapi.domain.IntList;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class SkifTestMappingDefaultTypeMapperW2DTest {
    SkifDefaultTypeMapperTestMapper mapper = new SkifDefaultTypeMapperTestMapper();
    SkifTestMapping map = mapper.getMapping();

    public void testMapString() {
        String source = "test";
        String target = map.w2d(source);
        assertNotNull(target);
        assertEquals(target, "test");
    }

    public void testMapInteger() {
        Integer source = 5;
        Integer target = map.w2d(source);
        assertThat(target).isEqualTo(5);
    }

    public void testMapInt() {
        int source = 5;
        int target = map.w2d(source);
        assertThat(target).isEqualTo(5);
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
        target = map.w2d(source, HashSet.class);
        assertEquals(target.size(), 2);
        assertEquals(target.iterator().next().getClass(), A.class);
    }

    /**
     * tester for å belyse 	SKIF-160
     */
    public void testMapTestC() {
        no.statkart.skif.skiftest.wsapi.domain.C source = new no.statkart.skif.skiftest.wsapi.domain.C();
        no.statkart.skif.skiftest.wsapi.domain.A a1 = new no.statkart.skif.skiftest.wsapi.domain.A();
        a1.setText("a1");
        no.statkart.skif.skiftest.wsapi.domain.A a2 = new no.statkart.skif.skiftest.wsapi.domain.A();
        a2.setText("a2");
        AList as = new AList();
        as.getItem().add(a1);
        as.getItem().add(a2);
        source.setAs(as);

        IntList ints = new IntList();
        ints.getItem().add(1);
        ints.getItem().add(2);
        ints.getItem().add(3);
        ints.getItem().add(4);
        source.setInts(ints);

        StringList strings = new StringList();
        strings.getItem().add("s1");
        strings.getItem().add("s2");
        strings.getItem().add("s3");
        source.setStrings(strings);

        no.statkart.skif.skiftest.domain.C target = map.w2d(source);
        assertEquals(Ints.asList(target.getInts()), source.getInts().getItem());
        assertEquals(target.getStrings(), source.getStrings().getItem().toArray());
        for (int i = 0; i < source.getAs().getItem().size(); i++) {
            assertEquals(target.getAs()[i].getText(), source.getAs().getItem().get(i).getText());
        }
    }

    public void testMapMap() {
        no.statkart.skif.skiftest.wsapi.domain.M source = new no.statkart.skif.skiftest.wsapi.domain.M();
        no.statkart.skif.skiftest.wsapi.domain.AMap mapOfA = new no.statkart.skif.skiftest.wsapi.domain.AMap();
        no.statkart.skif.skiftest.wsapi.domain.AMap.Entry entry = new no.statkart.skif.skiftest.wsapi.domain.AMap.Entry();
        entry.setKey("Foo");
        no.statkart.skif.skiftest.wsapi.domain.A a = new no.statkart.skif.skiftest.wsapi.domain.A();
        a.setText("Bar");
        no.statkart.skif.skiftest.wsapi.domain.AList aList = new no.statkart.skif.skiftest.wsapi.domain.AList();
        aList.getItem().add(a);
        entry.setValue(aList);
        mapOfA.getEntry().add(entry);
        source.setMapOfAs(mapOfA);

        M target = map.w2d(source);

        assertEquals(target.getMapOfAs().size(), 1);
        assertTrue(target.getMapOfAs().containsKey("Foo"));
        Set<A> aSet = target.getMapOfAs().get("Foo");
        assertEquals(aSet.size(), 1);
        assertEquals(aSet.iterator().next().getText(), "Bar");
    }
}

