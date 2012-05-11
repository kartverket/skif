package no.statkart.skif.storetest.domain;


import com.google.inject.Inject;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.storetest.domain.demo.Baz;
import no.statkart.skif.storetest.domain.demo.BazId;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class KodeTest extends StoreTestTestCase {
    @Inject
    Store store;

    public void testGetEnumKode() {
        AEnumKode aEnumKode = store.get(AEnumKodeId.KodeAId);
        assertEquals(aEnumKode.getId(), AEnumKodeId.KodeAId);
        assertEquals(aEnumKode.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
        Kodeliste kodeliste = store.get(aEnumKode.getKodelisteId());
        assertThat(kodeliste.getKodeIds()).containsExactly(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeAId, AEnumKodeId.KodeBId);
    }


    public void testGetEnumKode_Old() {
        AEnumKode aEnumKode = store.get(AEnumKodeId.KodeAId.asSnapshotVersionOld());
        assertEquals(aEnumKode.getId(), AEnumKodeId.KodeAId.asSnapshotVersionOld());
        assertEquals(aEnumKode.getKodelisteId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
        Kodeliste kodeliste = store.get(aEnumKode.getKodelisteId());
        assertThat(kodeliste.getKodeIds()).containsExactly(
                AEnumKodeId.IkkeOppgittId.asSnapshotVersionOld(),
                AEnumKodeId.KodeAId.asSnapshotVersionOld(),
                AEnumKodeId.KodeBId.asSnapshotVersionOld()
        );
    }


    public void testLoadADbKode() {
        ADbKode aDbKode_A1 = store.get(ADbKodeId.A1Id);
        assertEquals(aDbKode_A1.getKodeverdi(), "A1");
        Class valueType = aDbKode_A1.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);
    }

    public void testLoadADbKode_Old() {
        ADbKode aDbKode_A1 = store.get(ADbKodeId.A1Id.asSnapshotVersionOld());
        assertEquals(aDbKode_A1.getKodeverdi(), "A1");
        assertEquals(aDbKode_A1.getId().getSnapshotVersion(), SnapshotVersion.OLD);
        Class valueType = aDbKode_A1.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);
    }


    public void testLoadBDbKode() {
        BDbKode bDbKode_B1 = store.get(BDbKodeId.B1Id);
        assertEquals(bDbKode_B1.getKodeverdi(), "B1");
        Class valueType = bDbKode_B1.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);
    }

    public void testLoadSubclassedKodeC1() {
        CDbKode cDbKode_C1A = store.get(C1DbKodeId.C1AId);
        assertEquals(cDbKode_C1A.getKodeverdi(), "C1A");
    }

    public void testLoadSubclassedKodeC2() {
        CDbKode cDbKode_C2B = store.get(C2DbKodeId.C2BId);
        assertEquals(cDbKode_C2B.getKodeverdi(), "C2B");
    }

    public void testLoadXStrKode() {
        XStrDbKode xStrDbKode_A = store.get(XStrDbKodeId.AId);
        assertEquals(xStrDbKode_A.getKodeverdi(), "X1");
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
        XStrDbKode xStrDbKode_A_Old = store.get(XStrDbKodeId.AId.asSnapshotVersionOld());

        assertEquals(xStrDbKode_A_Current.getId().asSnapshotVersionCurrent(), XStrDbKodeId.AId);
        assertEquals(xStrDbKode_A_Old.getId().asSnapshotVersionCurrent(), XStrDbKodeId.AId);

        assertTrue(xStrDbKode_A_Old.getId().equalsIgnoreSnapshotVersion(XStrDbKodeId.AId));

        assertFalse(xStrDbKode_A_Old.getId().equals(XStrDbKodeId.AId));

    }

    /**
     * Koder bør ha egen metode som gir kompileringsfeil ved sammenlikning av koder av forskjellig type og som
     * ikke tar hensyn til SnapshotVersion
     */
    public void testEqualsTo() {
        XStrDbKode xStrDbKode_A_Current = store.get(XStrDbKodeId.AId);
        XStrDbKode xStrDbKode_A_Old = store.get(XStrDbKodeId.AId.asSnapshotVersionOld());

        assertTrue(XStrDbKodeId.AId.equalTo(xStrDbKode_A_Current.getId()));
        assertTrue(XStrDbKodeId.AId.equalTo(xStrDbKode_A_Old.getId()));

        // Skal gi kompileringsfeil:
        //XStrDbKodeId.AId.equalTo(AEnumKodeId.KodeAId);
    }

    public void testLoadBaz() {
        Baz obj = store.get(new BazId<Baz>(502L));
        Assert.assertEquals(obj.getTestAEnumKodeId(), AEnumKodeId.KodeAId);
        Assert.assertEquals(obj.getTestC2DbKodeId(), C2DbKodeId.C2BId);
    }

    public void testEquals() {
        StoreTestKodelisteLongId<StoreTestKodelisteLong> kodelisteId = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);

        assertEquals(kodelisteId, kodelisteId1);
        assertEquals(kodelisteId, kodelisteId2);
        assertEquals(kodelisteId1, kodelisteId2);
    }

    public void testNotEquals() {
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(2L);

        assertFalse(kodelisteId1.equals(kodelisteId2));
    }

    public void testCreateInstance() {
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestKodelisteLongId<StoreTestKodelisteLong>(1L);
        StoreTestKodelisteLong kodeliste = kodelisteId1.createTypeInstance();
        assertNull(kodeliste.getId());
        assertEquals(kodeliste.getKodeIds().size(), 0);
    }

    public void testGetKode() {
        AEnumKode kode = store.get(AEnumKodeId.KodeAId);
        assertNotNull(kode);
    }

    public void testGetLongKodeliste() {
        Kodeliste kodeliste = store.get(AEnumKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
    }

    public void testGetStringKodeliste() {
        Kodeliste kodeliste = store.get(SEnumKodeId.KODELISTE_ID);
        StoreTestKodelisteString storeTestEnumKodelisteString = store.get(SEnumKodeId.KODELISTE_ID);
        assertSame(kodeliste, storeTestEnumKodelisteString);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
        assertEquals(list.size(), 3);
    }

    public void testGetStringKode() {
        StoreTestKodelisteLong kodeliste = store.get(XStrDbKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
        assertEquals(list.size(), 2);
    }


    public void testGetKodelisteTransfer() {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        KodelisteTransfer<StoreTestKodelisteId<?>> kodelisteTransfer = kodelisteService.getKodelister(SnapshotVersion.CURRENT);
        List<? extends StoreTestKodelisteId<?>> kodelisteIds = kodelisteTransfer.getKodelisteIds();

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
        List list = store.get(kodelisteTransfer.getKodelisteIds());

        assertNotNull(kodelisteTransfer);
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