package no.statkart.skif.storetest.domain;


import com.google.inject.Provider;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.*;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateVersionFactory;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.kodeliste.DbKodelisteLoader;
import no.statkart.skif.store.persistence.kodeliste.KodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.Baz;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase5;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(enabled = false)
public class KodelisteTest extends StoreTestTestCase5 {

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


    public void testEnumKodeLokale() {
        KodelisteService kodelisteService = injector.getInstance(KodelisteService.class);
        Store store = injector.getInstance(Store.class);

        ServiceContext context = injector.getInstance(ServiceContext.class);
        context.setLocale(new Locale("no", "NO"));

        Kode enumKode = store.get(CEnumKodeId.KodeAId);
        assertNotNull(enumKode);
        assertFalse(enumKode.getBeskrivelse().contains("(nynorsk)"));

        Collection<? extends KodelisteId> kodelisteIds = kodelisteService.getKodelisteIds();
        assertNotNull(kodelisteIds);
        for (KodelisteId listeId : kodelisteIds) {
            BubbleObject o = store.get(listeId);
            if (o instanceof Kodeliste) {
                Kodeliste liste = (Kodeliste) o;
                for (Kode kode : liste.getKoder()) {
                    String beskrivelse = kode.getBeskrivelse();
                    assertNotNull(beskrivelse);
                    assertFalse(beskrivelse.contains("."));
                    assertFalse(beskrivelse.contains("(nynorsk)"));
                }
            }
        }

        store.clear();
        context.setLocale(new Locale("no", "NO","NY"));

        enumKode = store.get(BEnumKodeId.KodeAId);
        assertNotNull(enumKode);
        assertTrue(enumKode.getBeskrivelse().contains("(nynorsk)"));

        Collection<? extends KodelisteId> kodelisteIdsNy = kodelisteService.getKodelisteIds();
        assertNotNull(kodelisteIdsNy);
         for (KodelisteId listeId : kodelisteIdsNy) {
            BubbleObject o = store.get(listeId);
            if (o instanceof Kodeliste) {
                Kodeliste liste = (Kodeliste) o;
                for (Kode kode : liste.getKoder()) {
                    String beskrivelse = kode.getBeskrivelse();
                    assertNotNull(beskrivelse);
                    assertFalse(beskrivelse.contains("."));
                    assertTrue(beskrivelse.contains("nynorsk"));
                }
            }
        }
    }

}
