package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.SubTypeWithCollection;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreTest extends StoreTestTestCase {
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store store;

    public void testStoreGet() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        List<SimpleId> ids = new ArrayList<SimpleId>();
        ids.add(simple1Id);

        Simple bubble = store.get(simple1Id);
        assertEquals(simple1Id, bubble.getId());

        List<Simple> bubbles = store.get(ids);
        assertEquals(1, bubbles.size());
        assertEquals(simple1Id, bubbles.get(0).getId());
    }


    public void testStoreGetOld() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simpleId1Old = mockupFacade.getSimpleMockupFactory().getSimpleId1().asSnapshotVersionOld();

        Simple bubble = store.get(simpleId1Old);
        assertEquals(simpleId1Old, bubble.getId());
        assertEquals(bubble.getId().getSnapshotVersion(), SnapshotVersion.OLD);
    }

//    @Test
//    public void testStoreGetFoo() {
//        Foo currentFoo = store.get(new FooId<Foo>(new Long(100), SnapshotVersion.CURRENT));
//        assertNotNull(currentFoo);
//        Assert.assertEquals(currentFoo.getNr(), 2200);
//        Assert.assertEquals(currentFoo.getNavn(), "KARTVEIEN");
//
//        Foo oldestFoo = store.get(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:00:30.00")));
//        Assert.assertNotNull(oldestFoo);
//        Assert.assertEquals(oldestFoo.getNr(), 2200);
//        Assert.assertEquals(oldestFoo.getNavn(), "KARTGATA");
//
//        Foo newerFoo = store.get(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:01:30.00")));
//        Assert.assertNotNull(newerFoo);
//        Assert.assertEquals(newerFoo.getNr(), 2200);
//        Assert.assertEquals(newerFoo.getNavn(), "KARTVEGEN");
//
//        Foo newerFoo2 = store.get(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:02:30.00")));
//        Assert.assertNotNull(newerFoo2);
//        Assert.assertEquals(newerFoo2.getNr(), 2200);
//        Assert.assertEquals(newerFoo2.getNavn(), "KARTVEIEN");
//
//        Foo newerFoo3 = store.get(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:03:30.00")));
//        Assert.assertNotNull(newerFoo3);
//        Assert.assertEquals(newerFoo3.getNr(), 2200);
//        Assert.assertEquals(newerFoo3.getNavn(), "KART-VEIEN");
//
//        Foo newerFoo4 = store.get(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:04:30.00")));
//        Assert.assertNotNull(newerFoo4);
//        Assert.assertEquals(newerFoo4.getNr(), 2200);
//        Assert.assertEquals(newerFoo4.getNavn(), "KARTVEIEN");
//
//        Assert.assertNotSame(newerFoo4, currentFoo); //Samme objekt men forskjellig snapshotversion!
//    }
//
//    /**
//     * Henter ut flere Foo med forskjellige snapshotversions i samme kall
//     */
//    @Test
//    public void testStoreGetFooFlereSnapshotVersions() {
//
//        ArrayList<FooId<Foo>> ids = new ArrayList<FooId<Foo>>();
//        ids.add(new FooId<Foo>(100L, SnapshotVersion.CURRENT));
//        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:00:30.00")));
//        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:01:30.00")));
//        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:06:30.00")));
//        List<Foo> foos = store.get(ids);
//
//        Assert.assertEquals(foos.size(), 4);
//
//    }
//
//    /**
//     * Tester uthenting av objekter basert på id'er i forskjellig rekkefølge.
//     */
//    @Test
//    public void testStoreGetManyFoos() {
//
//        FooId<Foo> fooId_100 = new FooId<Foo>(100L);
//        FooId<Foo> fooId_101 = new FooId<Foo>(101L);
//        FooId<Foo> fooId_100_080030 = new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:00:30.00"));
//        FooId<Foo> fooId_100_080130 = new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:01:30.00"));
//
//        assertThat(store.get(Arrays.asList(fooId_101, fooId_100))).hasSize(2);
//        assertThat(extractProperty("id.value").from(store.get(Arrays.asList(fooId_101, fooId_100)))).contains(101L, 100L);
//    }
//
//    @Test
//    public void testStoreGetBar() {
//        Bar bar = store.get(new BarId<Bar>(1001L));
//        Assert.assertEquals(bar.getHusnr(), 106);
//        Assert.assertEquals(bar.getBokstav(), null);
//        Assert.assertEquals(bar.getFooId().getValue(), new Long(100));
//        Assert.assertEquals(bar.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//        Assert.assertEquals(bar.getFooId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//        Assert.assertEquals(bar.getBazId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//
//        Baz baz = store.get(bar.getBazId());
//        Assert.assertEquals(baz.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//        Assert.assertEquals(baz.getFooId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//
//
//        Foo foo = store.get(bar.getFooId());
//        assertEquals(foo.getNavn(), "KARTVEIEN");
//
//        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:03:15.00");
//        Bar olderBar = store.get(new BarId<Bar>(1001L, snapshotVersion));
//        Assert.assertEquals(olderBar.getHusnr(), 105);
//        Assert.assertEquals(olderBar.getBokstav(), null);
//        Assert.assertEquals(olderBar.getFooId().getValue(), new Long(100));
//        Assert.assertEquals(olderBar.getId().getSnapshotVersion(), snapshotVersion);
//        Assert.assertEquals(olderBar.getFooId().getSnapshotVersion(), snapshotVersion);
//
//        baz = store.get(olderBar.getBazId());
//        Assert.assertEquals(baz.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//        Assert.assertEquals(baz.getFooId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//
//
//        Foo olderFoo = store.get(olderBar.getFooId());
//        assertEquals(olderFoo.getNavn(), "KART-VEIEN");
//
//    }
//
//    public void testGetChildObject() {
//        StoreService store = injector.getInstance(StoreService.class);
//        ChildBubble childBubble = store.getObject(new ChildBubbleId<ChildBubble>(1L, SnapshotVersion.CURRENT));
//        assertNotNull(childBubble);
//    }
//
//    public void testGetBarOld() {
//        StoreService store = injector.getInstance(StoreService.class);
//        Bar bar = store.getObject(new BarId<Bar>(1001L, SnapshotVersion.OLD));
//
//        Assert.assertEquals(bar.getHusnr(), 106);
//        Assert.assertEquals(bar.getBokstav(), null);
//        Assert.assertEquals(bar.getFooId().getValue(), new Long(100));
//        Assert.assertEquals(bar.getId().getSnapshotVersion(), SnapshotVersion.OLD);
//        Assert.assertEquals(bar.getFooId().getSnapshotVersion(), SnapshotVersion.OLD);
//        Assert.assertEquals(bar.getBazId().getSnapshotVersion(), SnapshotVersion.OLD);
//        Baz baz = store.getObject(bar.getBazId());
//        Assert.assertEquals(baz.getId().getSnapshotVersion(), SnapshotVersion.OLD);
//        Assert.assertEquals(baz.getFooId().getSnapshotVersion(), SnapshotVersion.OLD);
//
//
//    }
//
//    public void testStoreGetBarFoos() {
//        Store store = injector.getInstance(Store.class); //Må bruke Store her istedenfor StoreService da man bruker intern store på objekter i testen
//        BarFoos barFoos = store.get(new BarFoosId<BarFoos>(2001L));
//        Assert.assertEquals(barFoos.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
//        Assert.assertEquals(barFoos.getBarId(), new BarId<Bar>(1001L));
//        Assert.assertEquals(barFoos.getBar().getId(), new BarId<Bar>(1001L)); //Bruker her store internt i objektet
//        Assert.assertEquals(barFoos.getFooIds(), Arrays.asList(new FooId<Foo>(100L), new FooId<Foo>(101L)));
//
//
//        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:03:15.00");
//        BarFoos olderBarFoos = store.get(new BarFoosId<BarFoos>(2001L, snapshotVersion));
//        Assert.assertEquals(olderBarFoos.getId().getSnapshotVersion(), snapshotVersion);
//        Assert.assertEquals(olderBarFoos.getBarId(), new BarId<Bar>(1001L, snapshotVersion));
//        Assert.assertEquals(olderBarFoos.getFooIds().size(), 0);
//
//    }
//
//    public void testStoreGetRaz() {
//        StoreService store = injector.getInstance(StoreService.class);
//
//        Raz raz = store.getObject(new RazId<Raz>(601L));
//        raz.getRazComponent().getFooId();
//
//    }
//
//
//    // TODO: Mapping mangler for webservice
//    @Test(groups = "singlevm-required")
//    public void testGetMedGenerellId() {
//
//        store.get(new RettsstiftelseId<Rettsstiftelse>(2001L, SnapshotVersion.CURRENT));
//        store.get(new ServituttId<Servitutt>(2001L, SnapshotVersion.CURRENT));
//
//        try {
//            store.get(new PengeheftelseId<Pengeheftelse>(2002L, SnapshotVersion.CURRENT));
//        } catch (ObjectNotFoundException oNFE) {
//            //OK
//        }
//        store.get(new RettsstiftelseId<Rettsstiftelse>(2002L, SnapshotVersion.CURRENT));
//        store.get(new ServituttId<Servitutt>(2003L, SnapshotVersion.CURRENT));
//
//    }

    /**
     * Tester insert object med automatisk tildeling av id
     */
    public void testManuellAllokeringAvId() {
        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            Simple Simple1 = new Simple();
            final SimpleId<?> nextId = store.getInstance(IdService.class).getNextId(SimpleId.class);
            assertNotNull(nextId.getValue());
            Simple1.setId(nextId);
            store.insert(Simple1);
        } finally {
            store.abortUnitOfWork(unitOfWork);
        }
    }

    /**
     * Tester insert object med automatisk tildeling av id
     */
    public void testAutomatiskTilordningAvIdViaInsert() {
        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            Simple simple1 = new Simple();
            simple1.setText("Insert Automatisk 1");
            assertNull(simple1.getId());
            store.insert(simple1);
            Simple simple2 = new Simple();
            simple2.setText("Insert Automatisk 2");
            assertNull(simple2.getId());
            store.insert(simple2);
            assertNotNull(simple2.getId());
            assertFalse(simple1.getId().equals(simple2.getId()));
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }
    }

    /**
     * Tester forsøk på henting av ikke-eksisterende objekt.
     */
    public void testObjectNotFoundException() {
        final SimpleId<?> id = new SimpleId(-1L);
        try {
            store.get(id);
            fail("Skulle fått exception");
        } catch (FinderException e) {
        }
//        TODO: bruke denne istedet
//        } catch (ObjectNotFoundException e) {
//            e.printStackTrace();
//            assertEquals(e.getNotFoundId(), id);
//        }
    }

    /**
     * Tester forsøk på henting av ikke-eksisterende objekt.
     */
    public void testObjectsNotFoundException() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        final List<SimpleId<?>> ids = Arrays.<SimpleId<?>>asList(simple1Id, new SimpleId(-1L));
        try {
            store.get(ids);
            fail("Skulle fått exception");
        } catch (FinderException e) {
        }
//        TODO: bruke denne istedet
//        } catch (ObjectsNotFoundException e) {
//            e.printStackTrace();
//            assertEquals(e.getIdsNotFound(), Collections.singleton(new SimpleId(-1L)));
//        }
    }

    /**
     * Tester forsøk på ignorering av ikke-eksisterende objekt.
     */

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return mockupFacade.getSimpleMockupFactory().getAllIds(SimpleId.class);
            }
        });
    }

    public void testIgnoreMissing() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        final List<SimpleId<?>> ids = Arrays.<SimpleId<?>>asList(simple1Id, new SimpleId(-1L));
        List<Simple> bubbles = store.getIgnoreMissing(ids);
        assertEquals(bubbles.size(), 1, "Antall objekter");
        assertEquals(bubbles.get(0).getId(), simple1Id, "Uventet id");
    }

    public void  testHentObjectMedEmptyCollection() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        SubTypeWithCollection objectWithEmptyCollection = (SubTypeWithCollection)store.get(mockupFacade.getSubTypedBubbleMockupFactory().getDifferentHistoricSubtypesId().asSnapshotVersion(SnapshotVersion.createInstance("2011-10-02 09:00:00.00")));
        assertNotNull(objectWithEmptyCollection.getTekster());
        assertTrue(objectWithEmptyCollection.getTekster().isEmpty());
        Collection<String> stringList = ImmutableList.of("a", "b");
        objectWithEmptyCollection.getTekster().addAll(stringList);

    }
}