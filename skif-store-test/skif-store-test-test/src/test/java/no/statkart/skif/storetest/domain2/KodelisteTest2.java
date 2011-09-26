package no.statkart.skif.storetest.domain2;


import no.statkart.skif.store2.KodeIdLookup2;
import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.Store2;
import no.statkart.skif.store2.kodelistesupport2.Kode2;
import no.statkart.skif.store2.kodelistesupport2.Kodeliste2;
import no.statkart.skif.storetest.service2.kodeliste2.KodelisteService2;
import no.statkart.skif.storetest.util.testsupport2.StoreTestTestCase2;
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
public class KodelisteTest2 extends StoreTestTestCase2 {

    public void testGetKode(){
        Store2 store = injector.getInstance(Store2.class);
            TestAEnumKode2 kode = store.get(TestAEnumKodeId2.KodeAId);
        assertNotNull(kode);
    }

    public void testGetKodeliste(){
        Store2 store = injector.getInstance(Store2.class);
        Kodeliste2 kodeliste = store.get(TestAEnumKodeId2.KODELISTE_ID);
        List<Kode2> list = store.get(kodeliste.getKodeIds());
        assertNotNull(list);
    }

    public void testGetKodelisteTransfer() {
        KodelisteService2 kodelisteService = injector.getInstance(KodelisteService2.class);
        KodelisteTransfer2 kodelisteTransfer = kodelisteService.getKodelister();
        assertNotNull(kodelisteTransfer);
    }

    public void testKodeIdLookup() {
        KodelisteService2 kodelisteService = injector.getInstance(KodelisteService2.class);
        Store2 store = injector.getInstance(Store2.class);
        KodelisteTransfer2 kodelisteTransfer = kodelisteService.getKodelister();
        List<Kode2> objects = new ArrayList<Kode2>();
        store.register(kodelisteTransfer.getObjects(), objects);
        KodeIdLookup2 kodeIdLookup = KodeIdLookup2.buildFromKodeliste((Collection<? extends Kodeliste2>) store.get(kodelisteTransfer.getKodelisteIds()));
        TestBEnumKodeId2 bKodeId = kodeIdLookup.fromKodeVerdi(TestBEnumKodeId2.class, "B");
        assertSame(bKodeId, TestBEnumKodeId2.KodeBId);
    }

                            


}
