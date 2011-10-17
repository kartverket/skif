package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.domain.A;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import no.statkart.skif.storetest.domain.kodeliste.TestADbKodeId;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

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

    public void testMapTestEnumkodelisteId() {
        TestEnumKodelisteId<TestEnumKodeliste> enumKodelisteId = new TestEnumKodelisteId<TestEnumKodeliste>(1);
        StoreTestBubbleId storeTestBubbleId = map.d2w(enumKodelisteId);
        assertSame(storeTestBubbleId.getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class);
        assertEquals(storeTestBubbleId.getValue(), "1");
    }

    public void testMapEnumKodelisteUtenKodeIds() {
        EnumKodeliste kodeliste = new TestEnumKodeliste();
        kodeliste.setId(new TestEnumKodelisteId(1));
        kodeliste.setKodeIdClass(TestAEnumKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.domain.TestAEnumKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 0);

        // Map wsapi->domain
        TestKodeliste domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), TestKodelisteImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), TestKodelisteIdImpl.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapEnumKodelisteMedKodeIds() {
        EnumKodeliste kodeliste = new TestEnumKodeliste();
        kodeliste.setId(new TestEnumKodelisteId(1));
        kodeliste.setKodeIdClass(TestAEnumKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        kodeIds.add(TestAEnumKodeId.KodeAId);
        kodeIds.add(TestAEnumKodeId.KodeBId);

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.TestAEnumKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 2);
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId kodeId = target.getKodeIds().getItem().get(0);
        assertEquals(kodeId.getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.TestAEnumKodeId.class);
        assertEquals(kodeId.getValue(), "1");
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId kodeId2 = target.getKodeIds().getItem().get(1);
        assertEquals(kodeId2.getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.TestAEnumKodeId.class);
        assertEquals(kodeId2.getValue(), "2");

        // Map wsapi->domain
        TestKodeliste domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), TestKodelisteImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), TestKodelisteIdImpl.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertSame(domainKodeliste.getKodeIds().get(0), TestAEnumKodeId.KodeAId);
        assertSame(domainKodeliste.getKodeIds().get(1),TestAEnumKodeId.KodeBId);
    }

    public void testMapDbKodelisteUtenKodeIds() {
        DbKodeliste kodeliste = new TestDbKodeliste();
        kodeliste.setId(new TestDbKodelisteId(1));
        kodeliste.setKodeIdClass(TestADbKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.kodeliste.TestADbKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 0);

        // Map wsapi->domain
        TestKodeliste domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), TestKodelisteImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), TestKodelisteIdImpl.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapDbKodelisteMedKodeIds() {
        DbKodeliste kodeliste = new TestDbKodeliste();
        kodeliste.setId(new TestDbKodelisteId(1));
        kodeliste.setKodeIdClass(TestADbKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");

        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        kodeIds.add(TestADbKodeId.A1Id);
        kodeIds.add(TestADbKodeId.A2Id);

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.kodeliste.TestADbKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 2);

        // Map wsapi->domain
        TestKodeliste domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), TestKodelisteImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), TestKodelisteIdImpl.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertSame(domainKodeliste.getKodeIds().get(0), TestADbKodeId.A1Id);
        assertSame(domainKodeliste.getKodeIds().get(1),TestADbKodeId.A2Id);
    }

}
