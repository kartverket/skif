package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.domain.demo.koder.*;
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
        StoreTestKodelisteLongId<StoreTestKodelisteLong> enumKodelisteId = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestBubbleId storeTestBubbleId = map.d2w(enumKodelisteId);
        assertSame(storeTestBubbleId.getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class);
        assertEquals(storeTestBubbleId.getValue(), "1");
    }

    public void testMapKodeLongId() {
        Object wsapiKodeId = map.d2w((Object) AEnumKodeId.KodeAId);
        Object domainKodeId = map.w2d(wsapiKodeId);
        assertEquals(domainKodeId, AEnumKodeId.KodeAId);
    }

    public void testMapKodeStringId() {
        Object wsapiKodeId = map.d2w((Object) XStrDbKodeId.AId);
        Object domainKodeId = map.w2d(wsapiKodeId);
        assertEquals(domainKodeId, XStrDbKodeId.AId);
    }


    public void testMapKodeString() {
        XStrDbKode xStrDbKode = new XStrDbKode();
        xStrDbKode.setId(XStrDbKodeId.AId);
        Object wsapiKode = map.d2w((Object) xStrDbKode);
        Object domainKode = map.w2d(wsapiKode);
        assertEquals(domainKode, xStrDbKode);
    }


    public void testMapEnumKodelisteUtenKodeIds() {
        Kodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
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
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapEnumKodelisteMedKodeIds() {
        Kodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
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
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertEquals(domainKodeliste.getKodeIds().get(0), AEnumKodeId.KodeAId);
        assertEquals(domainKodeliste.getKodeIds().get(1), AEnumKodeId.KodeBId);
    }

    public void testMapDbKodelisteUtenKodeIds() {
        Kodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
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
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapDbKodelisteMedKodeIds() {
        Kodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
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
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse(), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn(), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertEquals(domainKodeliste.getKodeIds().get(0), ADbKodeId.A1Id);
        assertEquals(domainKodeliste.getKodeIds().get(1), ADbKodeId.A2Id);
    }

}
