package no.statkart.skif.storetest.domain;


import com.google.inject.Inject;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.basic.BubbleWithKode;
import no.statkart.skif.storetest.domain.demo.koder.ADbKode;
import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BDbKode;
import no.statkart.skif.storetest.domain.demo.koder.BDbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKode;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CDbKode;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKode;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class KodeTest extends StoreTestTestCase {
    @Inject
    Store store;
    @Inject
    private StoreUpdateService updateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    public void testGetEnumKode() {
        AEnumKode aEnumKode = store.get(AEnumKodeId.KodeAId);
        Assert.assertEquals(aEnumKode.getId(), AEnumKodeId.KodeAId);
        Assert.assertEquals(aEnumKode.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
        Kodeliste kodeliste = store.get(aEnumKode.getKodelisteId());
        assertThat(kodeliste.getKoderIds()).containsExactly(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeAId, AEnumKodeId.KodeBId);
    }


    public void testGetEnumKode_Old() {
        AEnumKode aEnumKode = store.get((AEnumKodeId) AEnumKodeId.KodeAId.asSnapshotVersionOld());
        Assert.assertEquals(aEnumKode.getId(), AEnumKodeId.KodeAId.asSnapshotVersionOld());
        Assert.assertEquals(aEnumKode.getKodelisteId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
        Kodeliste kodeliste = store.get(aEnumKode.getKodelisteId());
        assertThat(kodeliste.getKoderIds()).containsExactly(
                (KodeId<?>)AEnumKodeId.IkkeOppgittId.asSnapshotVersionOld(),
                (KodeId<?>)AEnumKodeId.KodeAId.asSnapshotVersionOld(),
                (KodeId<?>)AEnumKodeId.KodeBId.asSnapshotVersionOld()
        );
    }


    public void testLoadADbKode() {
        ADbKode aDbKode_A1 = store.get(ADbKodeId.A1Id);
        Assert.assertEquals(aDbKode_A1.getKodeverdi(), "A1");
        Class valueType = aDbKode_A1.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);
    }

    public void testLoadADbKode_Old() {
        ADbKode aDbKode_A1 = store.get((ADbKodeId) ADbKodeId.A1Id.asSnapshotVersionOld());
        Assert.assertEquals(aDbKode_A1.getKodeverdi(), "A1");
        Assert.assertEquals(aDbKode_A1.getId().getSnapshotVersion(), SnapshotVersion.OLD);
        Class valueType = aDbKode_A1.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);
    }


    public void testLoadBDbKode() {
        BDbKode bDbKode_B1 = store.get(BDbKodeId.B1Id);
        Assert.assertEquals(bDbKode_B1.getKodeverdi(), "B1");
        Class valueType = bDbKode_B1.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);
    }

    public void testLoadSubclassedKodeC1() {
        CDbKode cDbKode_C1A = store.get(C1DbKodeId.C1AId);
        Assert.assertEquals(cDbKode_C1A.getKodeverdi(), "C1A");
    }

    public void testLoadSubclassedKodeC2() {
        CDbKode cDbKode_C2B = store.get(C2DbKodeId.C2BId);
        Assert.assertEquals(cDbKode_C2B.getKodeverdi(), "C2B");
    }

    public void testLoadXStrKode() {
        XStrDbKode xStrDbKode_A = store.get(XStrDbKodeId.AId);
        Assert.assertEquals(xStrDbKode_A.getKodeverdi(), "X1");
        Class valueType = xStrDbKode_A.getId().getValueType();
        Assert.assertEquals(valueType, String.class);
    }

    /**
     * Test sammenlikning av koder. Ved sammenlikning mot konstanter bør man alltid konverterer til
     * SnapshotVersion.CURRENT først eller bruke equalsIgnoreSnapshotVersion
     *
     */
    public void testKodeEquals() {
        XStrDbKode xStrDbKode_A_Current = store.get(XStrDbKodeId.AId);
        XStrDbKode xStrDbKode_A_Old = store.get((XStrDbKodeId) XStrDbKodeId.AId.asSnapshotVersionOld());

        Assert.assertEquals(xStrDbKode_A_Current.getId().asSnapshotVersionCurrent(), XStrDbKodeId.AId);
        Assert.assertEquals(xStrDbKode_A_Old.getId().asSnapshotVersionCurrent(), XStrDbKodeId.AId);

        Assert.assertTrue(xStrDbKode_A_Old.getId().equalsIgnoreSnapshotVersion(XStrDbKodeId.AId));

        Assert.assertFalse(xStrDbKode_A_Old.getId().equals(XStrDbKodeId.AId));

    }

    /**
     * Koder bør ha egen metode som gir kompileringsfeil ved sammenlikning av koder av forskjellig type og som
     * ikke tar hensyn til SnapshotVersion
     */
    public void testEqualsTo() {
        XStrDbKode xStrDbKode_A_Current = store.get(XStrDbKodeId.AId);
        XStrDbKode xStrDbKode_A_Old = store.get((XStrDbKodeId) XStrDbKodeId.AId.asSnapshotVersionOld());

        Assert. assertTrue(XStrDbKodeId.AId.equalTo(xStrDbKode_A_Current.getId()));
        Assert.assertTrue(XStrDbKodeId.AId.equalTo(xStrDbKode_A_Old.getId()));

        // Skal gi kompileringsfeil:
        //XStrDbKodeId.AId.equalTo(AEnumKodeId.KodeAId);
    }

    public void testLoadBubbleWithKode() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        BubbleWithKode obj = store.get(mockupFacade.getBubbleWithKodeMockupFactory().getBubbleWithKodeId1());
        Assert.assertEquals(obj.getTestAEnumKodeId(), AEnumKodeId.KodeAId);
        Assert.assertEquals(obj.getTestC2DbKodeId(), C2DbKodeId.C2BId);
    }

    public void testEquals() {
        StoreTestKodelisteLongId<StoreTestKodelisteLong> kodelisteId = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);

        Assert.assertEquals(kodelisteId, kodelisteId1);
        Assert.assertEquals(kodelisteId, kodelisteId2);
        Assert.assertEquals(kodelisteId1, kodelisteId2);
    }

    public void testNotEquals() {
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(2L);

        Assert.assertFalse(kodelisteId1.equals(kodelisteId2));
    }

    public void testCreateInstance() {
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLong kodeliste = kodelisteId1.createTypeInstance();
        assertNull(kodeliste.getId());
        Assert.assertEquals(kodeliste.getKoderIds().size(), 0);
    }

    public void testGetKode() {
        AEnumKode kode = store.get(AEnumKodeId.KodeAId);
        Assert.assertNotNull(kode);
    }

    public void testGetLongKodeliste() {
        Kodeliste kodeliste = store.get(AEnumKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKoderIds());
        Assert.assertNotNull(list);
    }

    public void testGetStringKodeliste() {
        Kodeliste kodeliste = store.get(SEnumKodeId.KODELISTE_ID);
        StoreTestKodelisteString storeTestEnumKodelisteString = store.get(SEnumKodeId.KODELISTE_ID);
        Assert.assertSame(kodeliste, storeTestEnumKodelisteString);
        List<Kode> list = store.get(kodeliste.getKoderIds());
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 3);
    }

    public void testGetStringKode() {
        StoreTestKodelisteLong kodeliste = store.get(XStrDbKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKoderIds());
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 2);
    }


    public void testGetKodelisteTransfer() {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        KodelisteTransfer<? extends KodelisteId<?>> kodelisteTransfer = kodelisteService.getKodelister(SnapshotVersion.CURRENT);
        List<KodelisteId<?>> kodelisteIds = (List<KodelisteId<?>>) kodelisteTransfer.getKodelisterIds(); // Ellers feiler det som følger på Java 8

        assertThat(kodelisteIds).contains(
                AEnumKodeId.KODELISTE_ID,
                BEnumKodeId.KODELISTE_ID,
                SEnumKodeId.KODELISTE_ID,
                ADbKodeId.KODELISTE_ID,
                BDbKodeId.KODELISTE_ID,
                C1DbKodeId.KODELISTE_ID,
                C2DbKodeId.KODELISTE_ID
                );

        store.evictAll(); // Sørg for å ha en ren Store, ellers blir det en situasjon SKIF-477 ikke tar høyde for
        store.register(kodelisteTransfer);
        List list = store.get(kodelisteIds);

        Assert.assertNotNull(kodelisteTransfer);
    }

    /**
     * Tester opprettelse og sletting av koder og tilhørende oppdatering av kodeliste. Testen viser
     * dagens rammeverk pt ikke støtter automatisk oppdatering av kodelisten på klient.
     *
     * TODO: Videre viser testen at inneværende versjon av SKIF ved store.register ikke refresher eksterende objekter
     */
    public void testInsertUpdateAndDeleteDbKode() {
        TestNumber testNumber = mockupFacadeFactory.getWriteMockupFacade().getTestNumber();
        Locale norsk = new Locale("no", "NO");
        LocalizedString localizedNavn = new LocalizedString();
        localizedNavn.setText(norsk, "Testkode");

        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        KodelisteTransfer<?> kodelisteTransfer = kodelisteService.getKodelister(SnapshotVersion.CURRENT);
        // TODO: store.register virker ikke når objekter finnes fra før. Denne linje kan tas vekk nå det er fixet
        store.evictAll();
        store.register(kodelisteTransfer);

        // Opprett en kode og test at den er med i ny kodeliste fra server
        C1DbKode c1DbKodeNew = new C1DbKode();
        c1DbKodeNew.setKodeverdi("NEW-" + testNumber.getNumber());
        c1DbKodeNew.setNavn(localizedNavn);
        UnitOfWork unitOfWork = store.beginUnitOfWork();
        store.insert(c1DbKodeNew);
        assertEquals(c1DbKodeNew.getKodelisteId(), C1DbKodeId.KODELISTE_ID);
        // TODO: Endre kodeliste til å bruke inversrelasjon slik at idlisten blir oppdatert automatisk på klient
        assertFalse(store.get(C1DbKodeId.KODELISTE_ID).getKoderIds().contains(c1DbKodeNew.getId()), "Forventet ikke at cachet kodelisten på klient  blir oppdatert automatisk når nye koder legges til");
        updateService.saveTransfer(store.getUnitOfWorkTransfer());
        unitOfWork.close();

        kodelisteTransfer = kodelisteService.getKodelister(SnapshotVersion.CURRENT);
        // TODO: store.register virker ikke når objekter finnes fra før. Denne linje kan tas vekk nå det er fixet
        store.evictAll();
        store.register(kodelisteTransfer);
        assertTrue(store.get(C1DbKodeId.KODELISTE_ID).getKoderIds().contains(c1DbKodeNew.getId()), "Forventet at kodeliste fra server har blitt oppdatert automatisk når nye koder har blitt lagt til");

        // Slett koden og test at er fjernet i kodelisten fra server.
        unitOfWork = store.beginUnitOfWork();
        CDbKode c1DbKodeToDelete = store.lock(c1DbKodeNew.getId());
        store.delete(c1DbKodeToDelete);
        // TODO: Endre kodeliste til å bruke inversrelasjon slik at idlisten blir oppdatert automatisk på klient
        assertTrue(store.get(C1DbKodeId.KODELISTE_ID).getKoderIds().contains(c1DbKodeNew.getId()), "Forventet ikke at cachet kodelisten på klient  blir oppdatert automatisk når koder fjernes");
        updateService.saveTransfer(store.getUnitOfWorkTransfer());
        unitOfWork.close();

        kodelisteTransfer = kodelisteService.getKodelister(SnapshotVersion.CURRENT);
        // TODO: store.register virker ikke når objekter finnes fra før. Denne linje kan tas vekk nå det er fixet
        store.evictAll();
        store.register(kodelisteTransfer);
        assertFalse(store.get(C1DbKodeId.KODELISTE_ID).getKoderIds().contains(c1DbKodeNew.getId()), "Forventet at kodeliste fra server har blitt oppdatert automatisk når koder fjernes");
    }
    
}
