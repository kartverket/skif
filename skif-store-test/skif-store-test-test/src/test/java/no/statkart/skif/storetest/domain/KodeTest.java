package no.statkart.skif.storetest.domain;


import com.google.inject.Inject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.basic.BubbleWithKode;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class KodeTest extends StoreTestTestCase {
    @Inject
    Store store;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    public void testGetEnumKode() {
        AEnumKode aEnumKode = store.get(AEnumKodeId.KodeAId);
        Assert.assertEquals(aEnumKode.getId(), AEnumKodeId.KodeAId);
        Assert.assertEquals(aEnumKode.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
        Kodeliste kodeliste = store.get(aEnumKode.getKodelisteId());
        assertThat(kodeliste.getKodeIds()).containsExactly(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeAId, AEnumKodeId.KodeBId);
    }


    public void testGetEnumKode_Old() {
        AEnumKode aEnumKode = store.get((AEnumKodeId) AEnumKodeId.KodeAId.asSnapshotVersionOld());
        Assert.assertEquals(aEnumKode.getId(), AEnumKodeId.KodeAId.asSnapshotVersionOld());
        Assert.assertEquals(aEnumKode.getKodelisteId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
        Kodeliste kodeliste = store.get(aEnumKode.getKodelisteId());
        assertThat(kodeliste.getKodeIds()).containsExactly(
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
        Assert.assertNull(kodeliste.getId());
        Assert.assertEquals(kodeliste.getKodeIds().size(), 0);
    }

    public void testGetKode() {
        AEnumKode kode = store.get(AEnumKodeId.KodeAId);
        Assert.assertNotNull(kode);
    }

    public void testGetLongKodeliste() {
        Kodeliste kodeliste = store.get(AEnumKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        Assert.assertNotNull(list);
    }

    public void testGetStringKodeliste() {
        Kodeliste kodeliste = store.get(SEnumKodeId.KODELISTE_ID);
        StoreTestKodelisteString storeTestEnumKodelisteString = store.get(SEnumKodeId.KODELISTE_ID);
        Assert.assertSame(kodeliste, storeTestEnumKodelisteString);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 3);
    }

    public void testGetStringKode() {
        StoreTestKodelisteLong kodeliste = store.get(XStrDbKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 2);
    }


    public void testGetKodelisteTransfer() {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        KodelisteTransfer<?> kodelisteTransfer = kodelisteService.getKodelister(SnapshotVersion.CURRENT);
        List<? extends KodelisteId> kodelisteIds = kodelisteTransfer.getKodelisteIds();

        assertThat(kodelisteIds).contains(
                AEnumKodeId.KODELISTE_ID,
                BEnumKodeId.KODELISTE_ID,
                SEnumKodeId.KODELISTE_ID,
                ADbKodeId.KODELISTE_ID,
                BDbKodeId.KODELISTE_ID,
                C1DbKodeId.KODELISTE_ID,
                C2DbKodeId.KODELISTE_ID
                );

        store.register(kodelisteTransfer);
        List list = store.get(kodelisteIds);

        Assert.assertNotNull(kodelisteTransfer);
    }

//    public void testKodeIdLookup() {
//        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
//        Store store = injector.getInstance(Store.class);
//        KodelisteTransfer kodelisteTransfer = kodelisteService.getKodelister();
//        List<Kode> objects = new ArrayList<Kode>();
//        store.register(kodelisteTransfer.getObjects(), objects);
//        KodeIdLookup kodeIdLookup = KodeIdLookup.buildFromKodeliste((Collection<? extends Kodeliste>) store.get(kodelisteTransfer.getKodelisteIds()));
//        BEnumKodeId bKodeId = kodeIdLookup.fromKodeVerdi(BEnumKodeId.class, "B");
//        assertSame(bKodeId, BEnumKodeId.KodeBId);
//    }


}