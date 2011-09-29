package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.kodelistesupport.EnumKodeliste;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteIdImpl;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteImpl;
import no.statkart.skif.storetest.domain.A;
import no.statkart.skif.storetest.domain.TestAEnumKodeId;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.AList;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreTestMappingD2WTest {
    StoreTestMapper configuration = new StoreTestMapper();
    StoreTestMapping map = configuration.getMapping();

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
        A source = new A();
        source.setText("10");
        no.statkart.skif.storetest.wsapi.domain.A target = map.d2w(source);
        assertNotNull(target);
        assertEquals(target.getText(), "10");


        target = map.d2w(source, no.statkart.skif.storetest.wsapi.domain.A.class);
        assertNotNull(target);
        assertEquals(target.getText(), "10");
    }


    /**
     * Tester mapping2 av et sett med API TestBubbleId objekter til en liste Web Service TestBubbleId objekter
     */
    public void testMapTestASet() {
        Set<A> source = new HashSet<A>();
        AList target = new AList();
        A a1 = new A();
        A a2 = new A();
        source.add(a1);
        source.add(a2);
        target = map.d2w(source, target);
        assertEquals(target.getItem().size(), 2);
        assertEquals(target.getItem().iterator().next().getClass(), no.statkart.skif.storetest.wsapi.domain.A.class);

        target = map.d2w(source, AList.class);
        assertEquals(target.getItem().size(), 2);
        assertEquals(target.getItem().iterator().next().getClass(), no.statkart.skif.storetest.wsapi.domain.A.class);
    }

    public void testMapTestBubble() {
        TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(10));
        testBubble.setText("test");
        no.statkart.skif.storetest.wsapi.domain.TestBubble target = map.d2w(testBubble);
        assertEquals(target.getId().getValue(), "10");
        assertEquals(target.getText(), "test");
    }

    /**
     * TODO: Se på hvorfor det blir match på flere mappere her!
     */
    @Test(enabled = false)
    public void testMapEnumliste() {
        EnumKodeliste kodeliste = new EnumKodelisteImpl();
        kodeliste.setId(new EnumKodelisteIdImpl(1));
        kodeliste.setKodeIdClass(TestAEnumKodeId.class);
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.kodeliste.TestAEnumKodeId");
    }
}
