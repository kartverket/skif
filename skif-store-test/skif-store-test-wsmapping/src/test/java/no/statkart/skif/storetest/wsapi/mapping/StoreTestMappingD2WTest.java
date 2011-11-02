package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import org.testng.annotations.Test;

import java.util.List;

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


    public void testMapTestBubble() {
        TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(10));
        testBubble.setText("test");
        no.statkart.skif.storetest.wsapi.domain.demo.TestBubble target = map.d2w(testBubble);
        assertEquals(target.getId().getValue(), "10");
        assertEquals(target.getText(), "test");
    }

    public void testMapTestEnumkodelisteId() {
        StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong> enumKodelisteId = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestBubbleId storeTestBubbleId = map.d2w(enumKodelisteId);
        assertSame(storeTestBubbleId.getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class);
        assertEquals(storeTestBubbleId.getValue(), "1");
    }

    public void testMapEnumKodelisteUtenKodeIds() {
        EnumKodeliste kodeliste = new StoreTestEnumKodelisteLong();
        kodeliste.setId(new StoreTestEnumKodelisteLongId(1));
        kodeliste.setKodeIdClass(AEnumKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.AEnumKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 0);

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLongImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteImplLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapEnumKodelisteMedKodeIds() {
        EnumKodeliste kodeliste = new StoreTestEnumKodelisteLong();
        kodeliste.setId(new StoreTestEnumKodelisteLongId(1));
        kodeliste.setKodeIdClass(AEnumKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        kodeIds.add(AEnumKodeId.KodeAId);
        kodeIds.add(AEnumKodeId.KodeBId);

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.AEnumKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 2);
        no.statkart.skif.storetest.wsapi.domain.kode.KodeId kodeId = target.getKodeIds().getItem().get(0);
        assertEquals(kodeId.getClass(), no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class);
        assertEquals(kodeId.getValue(), "1");
        no.statkart.skif.storetest.wsapi.domain.kode.KodeId kodeId2 = target.getKodeIds().getItem().get(1);
        assertEquals(kodeId2.getClass(), no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class);
        assertEquals(kodeId2.getValue(), "2");

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLongImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteImplLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertSame(domainKodeliste.getKodeIds().get(0), AEnumKodeId.KodeAId);
        assertSame(domainKodeliste.getKodeIds().get(1), AEnumKodeId.KodeBId);
    }

    public void testMapDbKodelisteUtenKodeIds() {
        DbKodeliste kodeliste = new StoreTestDbKodelisteLong();
        kodeliste.setId(new StoreTestDbKodelisteLongId(1));
        kodeliste.setKodeIdClass(ADbKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.ADbKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 0);

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLongImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteImplLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapDbKodelisteMedKodeIds() {
        DbKodeliste kodeliste = new StoreTestDbKodelisteLong();
        kodeliste.setId(new StoreTestDbKodelisteLongId(1));
        kodeliste.setKodeIdClass(ADbKodeId.class);
        kodeliste.setBeskrivelse("Test beskrivelse");
        kodeliste.setNavn("MyTestKodeliste");

        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        kodeIds.add(ADbKodeId.A1Id);
        kodeIds.add(ADbKodeId.A2Id);

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.ADbKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class );
        assertEquals(target.getBeskrivelse(), "Test beskrivelse");
        assertEquals(target.getNavn(), "MyTestKodeliste");
        assertEquals(target.getKodeIds().getItem().size(), 2);

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLongImpl.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteImplLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), 1L);
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertSame(domainKodeliste.getKodeIds().get(0), ADbKodeId.A1Id);
        assertSame(domainKodeliste.getKodeIds().get(1), ADbKodeId.A2Id);
    }

}
