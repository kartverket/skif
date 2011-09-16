package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.BubbleKodeIdLookup;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodelistesupport.BubbleKode;
import no.statkart.skif.store.kodelistesupport.BubbleKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 0.5
 */
@Test
public class KodelisteTest extends StoreTestTestCase {

    public void testGetKode(){
        Store store = injector.getInstance(Store.class);
        TestAEnumKode kode = store.get(TestAEnumKodeId.KodeAId);
        assertNotNull(kode);
    }

    public void testGetKodeliste(){
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

    public void testBubbleKodeIdLookup() {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        Store store = injector.getInstance(Store.class);
        KodelisteTransfer kodelisteTransfer = kodelisteService.getKodelister();
        List<BubbleKode> objects = new ArrayList<BubbleKode>();
        store.register(kodelisteTransfer.getObjects(), objects);
        BubbleKodeIdLookup kodeIdLookup = BubbleKodeIdLookup.buildFromKodeliste((Collection<? extends BubbleKodeliste>) store.get(kodelisteTransfer.getKodelisteIds()));
        TestBEnumKodeId bKodeId = kodeIdLookup.fromKodeVerdi(TestBEnumKodeId.class, "B");
        assertSame(bKodeId, TestBEnumKodeId.KodeBId);
    }

                            


}
