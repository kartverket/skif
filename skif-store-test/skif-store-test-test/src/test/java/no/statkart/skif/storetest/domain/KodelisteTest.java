package no.statkart.skif.storetest.domain;


import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import no.statkart.skif.store.KodeIdLookup;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.Kodeliste;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.ktest.MyList4;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;
import no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteServiceWS;
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

    @Test(enabled = false)
    public void testKodeIdLookup() throws ServiceException {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);

        StoreTestContext storeTestContext = new StoreTestContext();
        storeTestContext.setLocale("no_NO");
        storeTestContext.setSystemVersion("1");
        no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService kodelisteWS= injector.getInstance(Key.get(new TypeLiteral<no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService>(){}));
        no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer kodelister = kodelisteWS.getKodelister(storeTestContext);
        Object kodelisterTest = kodelisteWS.getKodelisterTest(storeTestContext);

        List<Kode> objects = new ArrayList<Kode>();
        Store store = injector.getInstance(Store.class);
        KodelisteTransfer kodelisteTransfer = kodelisteService.getKodelister();



//        store.register(kodelisteTransfer.getObjects(), objects);
//        KodeIdLookup kodeIdLookup = KodeIdLookup.buildFromKodeliste((Collection<? extends Kodeliste>) store.get(kodelisteTransfer.getKodelisteIds()));
//        TestBEnumKodeId bKodeId = kodeIdLookup.fromKodeVerdi(TestBEnumKodeId.class, "B");
//        assertSame(bKodeId, TestBEnumKodeId.KodeBId);
    }


    public void testMyList() throws ServiceException {
        StoreTestContext storeTestContext = new StoreTestContext();
        storeTestContext.setLocale("no_NO");
        storeTestContext.setSystemVersion("1");
        no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService kodelisteWS= injector.getInstance(Key.get(new TypeLiteral<no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService>() {
        }));
        Object kodelisterTest = kodelisteWS.getMyList(storeTestContext);
        assertNotNull(kodelisterTest);

    }

    public void testMyList2() throws ServiceException {
        StoreTestContext storeTestContext = new StoreTestContext();
        storeTestContext.setLocale("no_NO");
        storeTestContext.setSystemVersion("1");
        no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService kodelisteWS= injector.getInstance(Key.get(new TypeLiteral<no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService>(){}));
        Object kodelisterTest = kodelisteWS.getMyList2(storeTestContext);
        assertNotNull(kodelisterTest);

    }
    public void testMyList3() throws ServiceException {
        StoreTestContext storeTestContext = new StoreTestContext();
        storeTestContext.setLocale("no_NO");
        storeTestContext.setSystemVersion("1");
        no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService kodelisteWS= injector.getInstance(Key.get(new TypeLiteral<no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService>(){}));
        Object kodelisterTest = kodelisteWS.getMyList3(storeTestContext);
        assertNotNull(kodelisterTest);

    }
    public void testMyList4() throws ServiceException {
        StoreTestContext storeTestContext = new StoreTestContext();
        storeTestContext.setLocale("no_NO");
        storeTestContext.setSystemVersion("1");
        no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService kodelisteWS= injector.getInstance(Key.get(new TypeLiteral<no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteService>(){}));
        MyList4 kodelisterTest = kodelisteWS.getMyList4(storeTestContext);
        assertNotNull(kodelisterTest);
        assertEquals(kodelisterTest.getItem().size(), 1);

    }
}
