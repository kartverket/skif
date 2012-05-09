package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.FilteredBubble;
import no.statkart.skif.storetest.domain.demo.FilteredBubbleId;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.service.histtest.HistTestService;
import no.statkart.skif.storetest.service.txmanagement.BeanManagedTxAService;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.FileAssert.fail;

/**
 * Test av read / write og finish filter.
 *
 * @author Jan Holmen
 * @since 2.1
 */
@Test
public class BubbleFilterTest extends StoreTestServerTestCase {
    FilteredBubbleId<FilteredBubble> filteredBubbleId_1 = new FilteredBubbleId<FilteredBubble>(1);
    FilteredBubbleId<FilteredBubble> filteredBubbleId_2 = new FilteredBubbleId<FilteredBubble>(2);
    FilteredBubbleId<FilteredBubble> filteredBubbleId_101 = new FilteredBubbleId<FilteredBubble>(101);
    FilteredBubbleId<FilteredBubble> filteredBubbleId_102 = new FilteredBubbleId<FilteredBubble>(102);


    @BeforeMethod
    public void deletePriviouslyWritenTestBubbles() {
        System.err.println("Delete first");

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                TestHelper.deletePriviouslyWritenTestBubbles(persistenceSessionForSnapshot);
                System.err.println("DONE!");
                return null;
            }
        });

    }


    private void insert(final FilteredBubble fb) {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer sstore;

            public Object run() {
                sstore.beginTransaction();
                sstore.insert(fb);
                sstore.commitTransaction();
                return null;
            }
        });
    }

//    //Burde ha hatt en service for oppdatering av objektet i stedefor denne metoden, men les ser ut til å virke etterpå uansett.
//    private void update(final FilteredBubble fb) {
//        server.runInBeanManagedTransaction(new RunOnServerMethod() {
//            @Inject
//            StoreServer sstore;
//
//            public Object run() {
//                sstore.beginTransaction();
//                FilteredBubble locked = sstore.lock(fb.getId());
//                locked.setFilterText(fb.getFilterText());
//                sstore.update(locked);
//                sstore.commitTransaction();
//                return null;
//            }
//        });
//    }

    public void testLesFilteredKlasse() {
        Store storeClient = injector.getInstance(Store.class);

        FilteredBubble filteredBubble = storeClient.get(filteredBubbleId_1);
        assertNotNull(filteredBubble);
        assertFalse(filteredBubble.getFilterText().contains("*"));

        FilteredBubble filteredBubble2 = storeClient.get(filteredBubbleId_2);
        assertNotNull(filteredBubble2);
        assertTrue(filteredBubble2.getFilterText().contains("*"));
    }

    public void testInsertFilteredKlasse() {
        FilteredBubble filteredBubble = new FilteredBubble(filteredBubbleId_101, "Insert ufiltrert 101", false, "Insert filtrert 101");
        insert(filteredBubble);

        Store storeClient = injector.getInstance(Store.class);
        FilteredBubble lest = storeClient.get(filteredBubble.getId());
        assertEquals(filteredBubble, lest);
    }

////    @Test(groups = "broken")
//    public void testInsertFilteredKlasse_aktivt_fileter() {
//        Store storeClient = injector.getInstance(Store.class);
//        String ftekst = "Skal overskrives på vei ned i basen 101";
//        FilteredBubble filteredBubble;
//        try {
//            filteredBubble = new FilteredBubble(filteredBubbleId_101, "Insert ufiltrert 101", true, ftekst);
//            insert(filteredBubble);
//            fail("skulle ha feilet. kan ikke inserte ett objekt som har status filtrert");
//        } catch (Exception e) {
//            System.out.println("Feilet 1");
//            //skal feile
//        }
//        try {
//            FilteredBubble filteredBubble1 = storeClient.get(filteredBubbleId_101);
//            fail("skal ikke klare å finne dette objektet");
//        } catch (ObjectNotFoundException e) {
//            System.out.println("Feilet 2");
//            //skal feile
//        }
//    }

//    @Test(groups = "broken")
//    public void testLesOppdaterFilteredKlasse() {
//        Store storeClient = injector.getInstance(Store.class);
//        FilteredBubble filteredBubble = storeClient.get(filteredBubbleId_2);
//        filteredBubble.setFilterText("skal ikke klare å oppdatere denne");
//
//        try {
//            update(filteredBubble);
//            fail("Skulle ha feilet, kan ikke oppdatere objekt som er filtrert!!");
//        } catch (Exception e) {
//            //skal feile
//        }
//
//        FilteredBubble lest = storeClient.get(filteredBubbleId_2);
//        assertEquals(filteredBubble, lest);
//    }

//    @Test(groups = "broken")
//    public void testOppdater() {
//        Store storeClient = injector.getInstance(Store.class);
//        FilteredBubble filteredBubble;
//        filteredBubble = new FilteredBubble(filteredBubbleId_102, "Insert ufiltrert 102", false, "en tekst");
//        insert(filteredBubble);
//
//        String oppdatertTekst = "oppdatert Tekst";
//        filteredBubble.setFilterText(oppdatertTekst);
//        update(filteredBubble);
//
//        FilteredBubble lest = storeClient.get(filteredBubbleId_102);
//        assertTrue(oppdatertTekst.equals(lest.getFilterText()));
//    }


    // Ser ut til at metoden som skal slette dynamisk data ikke gjør jobben og så feiler denne testen fordi data fra annet testtilfelle ligger igjen
    @Test(groups = "broken")
    public void testFinish_ok() {
        System.err.println("testFinish_ok");
        deletePriviouslyWritenTestBubbles();
        Store storeClient = injector.getInstance(Store.class);
        FilteredBubble filteredBubble;
        filteredBubble = new FilteredBubble(filteredBubbleId_101, "Finish 101", false, "en tekst");
        insert(filteredBubble);

        FilteredBubble lest = storeClient.get(filteredBubbleId_101);
        assertTrue("overskrevet".equals(lest.getFilterText()));
    }

    @Test(groups = "broken")
    public void testFinish_fail() {
        Store storeClient = injector.getInstance(Store.class);
        FilteredBubble filteredBubble;
        filteredBubble = new FilteredBubble(filteredBubbleId_101, "Fail 101", false, "en tekst");
        try {
            insert(filteredBubble);
            fail("Objektets skal ikke kunne insertes");
        } catch (Exception e) {
            //skal feile
        }

        try {
            FilteredBubble lest = storeClient.get(filteredBubbleId_101);
            fail("Objektets skal ikke finnes");
        } catch (Exception e) {
            //skal feile
        }
    }


}
