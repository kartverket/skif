package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.KodeIdLookup;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.Kodeliste;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 0.5
 */
@Test
public class KodelisteTest extends StoreTestTestCase {

    public void testEquals() {
        TestEnumKodelisteId<TestEnumKodeliste> enumKodelisteId = new TestEnumKodelisteId<TestEnumKodeliste>(1);
        TestKodelisteId<?> kodelisteId1 = new TestEnumKodelisteId<TestEnumKodeliste>(1);
        TestKodelisteId<?> kodelisteId2 = new TestKodelisteIdImpl<TestKodelisteImpl>(1);

        assertEquals(enumKodelisteId, kodelisteId1);
        assertEquals(enumKodelisteId, kodelisteId2);
        assertEquals(kodelisteId1, kodelisteId2);
    }

    public void testNotEquals() {
        TestKodelisteId<?> kodelisteId1 = new TestEnumKodelisteId<TestEnumKodeliste>(1);
        TestKodelisteId<?> kodelisteId2 = new TestKodelisteIdImpl<TestKodelisteImpl>(2);

        assertFalse(kodelisteId1.equals(kodelisteId2));
    }

    public void testCreateInstance() {
        TestKodelisteId<?> kodelisteId1 = new TestEnumKodelisteId<TestEnumKodeliste>(1);
        TestKodeliste kodeliste = kodelisteId1.createTypeInstance();
        assertNull(kodeliste.getId());
        assertEquals(kodeliste.getKodeIds().size(), 0);
    }

    public void testGetKode() {
        Store store = injector.getInstance(Store.class);
        TestAEnumKode kode = store.get(TestAEnumKodeId.KodeAId);
        assertNotNull(kode);
    }

    public void testGetKodeliste() {
        Store store = injector.getInstance(Store.class);
        Kodeliste kodeliste = store.get(TestAEnumKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
    }

    public void testGetKodelisteTransfer() {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        KodelisteTransfer kodelisteTransfer = kodelisteService.getKodelister();
        assertNotNull(kodelisteTransfer);
    }

    public void testKodeIdLookup() {
         KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
         Store store = injector.getInstance(Store.class);
         KodelisteTransfer kodelisteTransfer = kodelisteService.getKodelister();
         List<Kode> objects = new ArrayList<Kode>();
         store.register(kodelisteTransfer.getObjects(), objects);
         KodeIdLookup kodeIdLookup = KodeIdLookup.buildFromKodeliste((Collection<? extends Kodeliste>) store.get(kodelisteTransfer.getKodelisteIds()));
         TestBEnumKodeId bKodeId = kodeIdLookup.fromKodeVerdi(TestBEnumKodeId.class, "B");
         assertSame(bKodeId, TestBEnumKodeId.KodeBId);
     }
}
