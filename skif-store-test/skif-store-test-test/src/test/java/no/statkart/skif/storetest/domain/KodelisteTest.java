package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.KodeIdLookup;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.Kodeliste;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
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
 * @since 0.5
 */
@Test
public class KodelisteTest extends StoreTestTestCase {

    public void testEquals() {
        StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong> enumKodelisteId = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteImplLongId<StoreTestKodelisteLongImpl>(1);

        assertEquals(enumKodelisteId, kodelisteId1);
        assertEquals(enumKodelisteId, kodelisteId2);
        assertEquals(kodelisteId1, kodelisteId2);
    }

    public void testNotEquals() {
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestKodelisteLongId<?> kodelisteId2 = new StoreTestKodelisteImplLongId<StoreTestKodelisteLongImpl>(2);

        assertFalse(kodelisteId1.equals(kodelisteId2));
    }

    public void testCreateInstance() {
        StoreTestKodelisteLongId<?> kodelisteId1 = new StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>(1);
        StoreTestKodelisteLong kodeliste = kodelisteId1.createTypeInstance();
        assertNull(kodeliste.getId());
        assertEquals(kodeliste.getKodeIds().size(), 0);
    }

    public void testGetKode() {
        Store store = injector.getInstance(Store.class);
        AEnumKode kode = store.get(AEnumKodeId.KodeAId);
        assertNotNull(kode);
    }

    public void testGetKodeliste() {
        Store store = injector.getInstance(Store.class);
        Kodeliste kodeliste = store.get(AEnumKodeId.KODELISTE_ID);
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
         BEnumKodeId bKodeId = kodeIdLookup.fromKodeVerdi(BEnumKodeId.class, "B");
         assertSame(bKodeId, BEnumKodeId.KodeBId);
     }
}
