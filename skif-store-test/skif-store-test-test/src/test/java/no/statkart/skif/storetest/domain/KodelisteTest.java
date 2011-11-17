package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.KodeIdLookup;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class KodelisteTest extends StoreTestTestCase {

    public void testEquals() {
        StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong> enumKodelisteId = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestEnumKodelisteLongId<?> kodelisteId1 = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteImplLongId<StoreTestKodelisteImplLong>(1);

        assertEquals(enumKodelisteId, kodelisteId1);
        assertEquals(enumKodelisteId, kodelisteId2);
        assertEquals(kodelisteId1, kodelisteId2);
    }

    public void testNotEquals() {
        StoreTestEnumKodelisteLongId<?> kodelisteId1 = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteImplLongId<StoreTestKodelisteImplLong>(2);

        assertFalse(kodelisteId1.equals(kodelisteId2));
    }

    public void testCreateInstance() {
        StoreTestEnumKodelisteLongId<?> kodelisteId1 = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestEnumKodelisteLong kodeliste = kodelisteId1.createTypeInstance();
        assertNull(kodeliste.getId());
        assertEquals(kodeliste.getKodeIds().size(), 0);
    }

    public void testGetKode() {
        Store store = injector.getInstance(Store.class);
        AEnumKode kode = store.get(AEnumKodeId.KodeAId);
        assertNotNull(kode);
    }

    public void testGetLongKodeliste() {
        Store store = injector.getInstance(Store.class);
        Kodeliste kodeliste = store.get(AEnumKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
    }

    @Test(enabled = false)
    public void testGetStringKodeliste() {
        Store store = injector.getInstance(Store.class);
        Kodeliste kodeliste = store.get(SEnumKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
        assertEquals(list.size(), 3);
    }

    public void testGetStringKode() {
        Store store = injector.getInstance(Store.class);
        StoreTestKodelisteLong kodeliste = store.get(XStrDbKodeId.KODELISTE_ID);
        List<Kode> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
        assertEquals(list.size(), 2);
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
         BEnumKodeId bKodeId = kodeIdLookup.fromKodeVerdi(BEnumKodeId.class, "B");
         assertSame(bKodeId, BEnumKodeId.KodeBId);
     }
}
