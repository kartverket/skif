package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private Locale norsk = new Locale("no", "NO");

    private LocalizedString createDomainLocalizedString(String text) {
        Map<Locale, String> map = Collections.singletonMap(norsk, text);
        return new LocalizedString(map);
    }
    
    private no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString createWsapiLocalizedString(String text) {
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry entry = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry();
        entry.setKey(norsk.toString());
        entry.setValue(text);
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString localizedString = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString();
        localizedString.getEntry().add(entry);
        return localizedString;
    }

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
        Simple simple = new Simple(new SimpleId<Simple>(10L));
        simple.setText("test");
        no.statkart.skif.storetest.wsapi.domain.basic.Simple target = map.d2w(simple, no.statkart.skif.storetest.wsapi.domain.basic.Simple.class);
        assertEquals(target.getId().getValue(), "10");
        assertEquals(target.getText(), "test");
    }

    public void testMapTestEnumkodelisteId() {
        StoreTestKodelisteLongId<StoreTestKodelisteLong> enumKodelisteId = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestBubbleId storeTestBubbleId = map.d2w(enumKodelisteId);
        assertSame(storeTestBubbleId.getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class);
        assertEquals(storeTestBubbleId.getValue(), "1");
    }

    public void testMapKodeLongId() {
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId wsapiKodeId = map.d2w(AEnumKodeId.KodeAId);
        StoreTestKodeId<?> domainKodeId = map.w2d(wsapiKodeId);
        assertEquals(domainKodeId, AEnumKodeId.KodeAId);
    }

    public void testMapKodeStringId() {
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId wsapiKodeId = map.d2w(XStrDbKodeId.AId);
        StoreTestKodeId<?> domainKodeId = map.w2d(wsapiKodeId);
        assertEquals(domainKodeId, XStrDbKodeId.AId);
    }


    public void testMapKodeString() {
        XStrDbKode xStrDbKode = new XStrDbKode();
        xStrDbKode.setId(XStrDbKodeId.AId);
        Object wsapiKode = map.d2w(xStrDbKode, no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode.class);
        Object domainKode = map.w2d(wsapiKode, XStrDbKode.class);
        assertEquals(domainKode, xStrDbKode);
    }


    public void testMapEnumKodelisteUtenKodeIds() {
        StoreTestKodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
        kodeliste.setKodeIdClass(AEnumKodeId.class);
        kodeliste.setBeskrivelse(createDomainLocalizedString("Test beskrivelse"));
        kodeliste.setNavn(createDomainLocalizedString("MyTestKodeliste"));

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste, no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class );
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiBeskrivelse = createWsapiLocalizedString("Test beskrivelse");
        assertEquals(target.getBeskrivelse().getEntry().size(), 1);
        assertEquals(target.getBeskrivelse().getEntry().get(0).getKey(), wsapiBeskrivelse.getEntry().get(0).getKey());
        assertEquals(target.getBeskrivelse().getEntry().get(0).getValue(), wsapiBeskrivelse.getEntry().get(0).getValue());
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiNavn = createWsapiLocalizedString("MyTestKodeliste");
        assertEquals(target.getNavn().getEntry().size(), 1);
        assertEquals(target.getNavn().getEntry().get(0).getKey(), wsapiNavn.getEntry().get(0).getKey());
        assertEquals(target.getNavn().getEntry().get(0).getValue(), wsapiNavn.getEntry().get(0).getValue());
        assertEquals(target.getKodeIds().getItem().size(), 0);

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target, StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse().getText(norsk), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn().getText(norsk), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapEnumKodelisteMedKodeIds() {
        StoreTestKodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
        kodeliste.setKodeIdClass(AEnumKodeId.class);
        kodeliste.setBeskrivelse(createDomainLocalizedString("Test beskrivelse"));
        kodeliste.setNavn(createDomainLocalizedString("MyTestKodeliste"));
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        kodeIds.add(AEnumKodeId.KodeAId);
        kodeIds.add(AEnumKodeId.KodeBId);

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste, no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class );
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiBeskrivelse = createWsapiLocalizedString("Test beskrivelse");
        assertEquals(target.getBeskrivelse().getEntry().size(), 1);
        assertEquals(target.getBeskrivelse().getEntry().get(0).getKey(), wsapiBeskrivelse.getEntry().get(0).getKey());
        assertEquals(target.getBeskrivelse().getEntry().get(0).getValue(), wsapiBeskrivelse.getEntry().get(0).getValue());
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiNavn = createWsapiLocalizedString("MyTestKodeliste");
        assertEquals(target.getNavn().getEntry().size(), 1);
        assertEquals(target.getNavn().getEntry().get(0).getKey(), wsapiNavn.getEntry().get(0).getKey());
        assertEquals(target.getNavn().getEntry().get(0).getKey(), wsapiNavn.getEntry().get(0).getKey());
        assertEquals(target.getKodeIds().getItem().size(), 2);
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId kodeId = target.getKodeIds().getItem().get(0);
        assertEquals(kodeId.getClass(), no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class);
        assertEquals(kodeId.getValue(), "1");
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId kodeId2 = target.getKodeIds().getItem().get(1);
        assertEquals(kodeId2.getClass(), no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class);
        assertEquals(kodeId2.getValue(), "2");

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target, StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse().getText(norsk), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn().getText(norsk), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertEquals(domainKodeliste.getKodeIds().get(0), AEnumKodeId.KodeAId);
        assertEquals(domainKodeliste.getKodeIds().get(1), AEnumKodeId.KodeBId);
    }

    public void testMapDbKodelisteUtenKodeIds() {
        StoreTestKodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
        kodeliste.setKodeIdClass(ADbKodeId.class);
        kodeliste.setBeskrivelse(createDomainLocalizedString("Test beskrivelse"));
        kodeliste.setNavn(createDomainLocalizedString("MyTestKodeliste"));

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste, no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class );
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiBeskrivelse = createWsapiLocalizedString("Test beskrivelse");
        assertEquals(target.getBeskrivelse().getEntry().size(), 1);
        assertEquals(target.getBeskrivelse().getEntry().get(0).getKey(), wsapiBeskrivelse.getEntry().get(0).getKey());
        assertEquals(target.getBeskrivelse().getEntry().get(0).getValue(), wsapiBeskrivelse.getEntry().get(0).getValue());
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiNavn = createWsapiLocalizedString("MyTestKodeliste");
        assertEquals(target.getNavn().getEntry().size(), 1);
        assertEquals(target.getNavn().getEntry().get(0).getKey(), wsapiNavn.getEntry().get(0).getKey());
        assertEquals(target.getNavn().getEntry().get(0).getValue(), wsapiNavn.getEntry().get(0).getValue());
        assertEquals(target.getKodeIds().getItem().size(), 0);

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target, StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse().getText(norsk), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn().getText(norsk), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 0);
    }

    public void testMapDbKodelisteMedKodeIds() {
        StoreTestKodeliste kodeliste = new StoreTestKodelisteLong();
        kodeliste.setId(new StoreTestKodelisteLongId(1L));
        kodeliste.setKodeIdClass(ADbKodeId.class);
        kodeliste.setBeskrivelse(createDomainLocalizedString("Test beskrivelse"));
        kodeliste.setNavn(createDomainLocalizedString("MyTestKodeliste"));

        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        kodeIds.add(ADbKodeId.A1Id);
        kodeIds.add(ADbKodeId.A2Id);

        // Map domain->wsapi
        no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste target = map.d2w(kodeliste, no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class);
        assertEquals(target.getKodeIdClass(), "no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId");
        assertEquals(target.getId().getValue(), "1" );
        assertEquals(target.getId().getClass(), no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class );
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiBeskrivelse = createWsapiLocalizedString("Test beskrivelse");
        assertEquals(target.getBeskrivelse().getEntry().size(), 1);
        assertEquals(target.getBeskrivelse().getEntry().get(0).getKey(), wsapiBeskrivelse.getEntry().get(0).getKey());
        assertEquals(target.getBeskrivelse().getEntry().get(0).getValue(), wsapiBeskrivelse.getEntry().get(0).getValue());
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiNavn = createWsapiLocalizedString("MyTestKodeliste");
        assertEquals(target.getNavn().getEntry().size(), 1);
        assertEquals(target.getNavn().getEntry().get(0).getKey(), wsapiNavn.getEntry().get(0).getKey());
        assertEquals(target.getNavn().getEntry().get(0).getValue(), wsapiNavn.getEntry().get(0).getValue());
        assertEquals(target.getKodeIds().getItem().size(), 2);

        // Map wsapi->domain
        StoreTestKodelisteLong domainKodeliste = map.w2d(target, StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getClass(), StoreTestKodelisteLong.class);
        assertEquals(domainKodeliste.getId().getClass(), StoreTestKodelisteLongId.class);
        assertEquals(domainKodeliste.getId().getValue(), new Long(1));
        assertEquals(domainKodeliste.getBeskrivelse().getText(norsk), "Test beskrivelse");
        assertEquals(domainKodeliste.getNavn().getText(norsk), "MyTestKodeliste");
        assertEquals(domainKodeliste.getKodeIds().size(), 2);
        assertEquals(domainKodeliste.getKodeIds().get(0), ADbKodeId.A1Id);
        assertEquals(domainKodeliste.getKodeIds().get(1), ADbKodeId.A2Id);
    }

}
