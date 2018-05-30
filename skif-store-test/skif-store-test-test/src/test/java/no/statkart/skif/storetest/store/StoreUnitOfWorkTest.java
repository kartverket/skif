package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.service.uow.UowTestService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.assertNotFound;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tester bruk av UnitOfWork på klient og server.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class StoreUnitOfWorkTest extends StoreTestMixedTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    private Store clientStore;

    @DataProvider(name = "boolean1Dmatrix")
    protected Object[][] boolean1Dmatrix() {
        return new Object[][]{
                {Boolean.TRUE},
                {Boolean.FALSE},
        };
    }

    @DataProvider(name = "boolean2Dmatrix")
    protected Object[][] boolean2Dmatrix() {
        return new Object[][]{
                {Boolean.TRUE, Boolean.TRUE},
                {Boolean.FALSE, Boolean.TRUE},
                {Boolean.TRUE, Boolean.FALSE},
                {Boolean.FALSE, Boolean.FALSE},
        };
    }

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(mockupFacade -> ImmutableSet.of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1(),
                mockupFacade.getSimpleMockupFactory().getSimpleId2()
        ));
    }

    public void testBeginEndEmptyUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                store.commitUnitOfWork(unitOfWork);
                return null;
            }
        });
    }

    public void testInsertObjectInUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple Simple = new Simple(simpleId, "Simple 101");
                store.insert(Simple);
                store.commitUnitOfWork(unitOfWork);
                assertSame(store.get(simpleId), Simple);
                store.flush();

                assertEquals(StandAloneTestHelper.countInDatabase(persistenceSessionForSnapshot, simpleId), 1);
                return null;
            }
        });
    }


    public void testInsertDeleteObjectInSameUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple Simple = new Simple(simpleId, "Simple 101");
                store.insert(Simple);
                store.delete(Simple);
                store.commitUnitOfWork(unitOfWork);
                assertNotFound(store, simpleId);

                return null;
            }
        });
    }


    public void testInsertDeleteObjectViaNestedUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                UnitOfWork unitOfWork1 = store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                UnitOfWork unitOfWork2 = store.beginUnitOfWork();
                Simple copy = CopyHelper.copy(simple);
                store.delete(copy);
                store.commitUnitOfWork(unitOfWork2);
//                assertSame(store.get(simpleId), copy);
                store.commitUnitOfWork(unitOfWork1);
                assertNotFound(store, simpleId);

                return null;
            }
        });
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testInsertDeleteObjectViaNestedUnitOfWork_Fail() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                store.beginUnitOfWork();
                store.delete(simple); //CopyHelper.copy(simple); // Bruker ved vilje feil instans her
                return null;
            }
        });
    }

    public void testUndoInsertion() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

                UnitOfWork unitOfWork = store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                store.undo(simple);
                store.commitUnitOfWork(unitOfWork);
                assertNotFound(store, simpleId);

                return null;
            }
        });
    }

    public void testUndoUpdate() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

                store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();
                Simple simple = store.lock(simpleId);
                store.update(simple);
                store.undo(simple);
                UnitOfWorkTransfer unitOfWorkTransfer = store.getUnitOfWorkTransfer();
                assertTrue(unitOfWorkTransfer.getUpdatedObjects().isEmpty());

                return null;
            }
        });

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

                UnitOfWork unitOfWork = store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();
                Simple simple = store.lock(simpleId);
                store.update(simple);
                String orgText = simple.getText();
                simple.setText("Blabla");
                store.update(simple);
                assertEquals(store.get(simpleId).getText(), simple.getText());
                store.undo(simple);
                assertEquals(store.get(simpleId).getText(), orgText);
                store.abortUnitOfWork(unitOfWork);

                return null;
            }
        });
    }

    public void testUndoDeletion() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

                store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();
                Simple simple = store.lock(simpleId);
                store.delete(simple);
                store.undo(simple);
                UnitOfWorkTransfer unitOfWorkTransfer = store.getUnitOfWorkTransfer();
                assertTrue(unitOfWorkTransfer.getDeletedObjects().isEmpty());

                return null;
            }
        });
    }

    public void testUndoInsertionReinsert() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

                UnitOfWork unitOfWork = store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                store.undo(simple);
                store.insert(simple);
                store.abortUnitOfWork(unitOfWork);

                return null;
            }
        });
    }

    public void testGetInUnitOfWork() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
                final SimpleId<?> id = mockupFacade.getSimpleMockupFactory().getSimpleId1();

                UnitOfWork unitOfWork = store.beginUnitOfWork();

                Simple Simple = store.get(id);
                assertNotNull(Simple);

                Set<Simple> Simples = store.get(Collections.singleton(id));
                assertEquals(Simples.size(), 1);
                assertNotNull(Simples.iterator().next());
                assertEquals(Simples.iterator().next(), Simple);

                store.abortUnitOfWork(unitOfWork);

                return null;
            }
        });
    }

    public void testGetTransferInsertNull() {
        //noinspection unused
        try (UnitOfWork uow1 = clientStore.beginUnitOfWork()) {
            Simple bubbleObject = new Simple();
            clientStore.insert(bubbleObject);

            //noinspection unused
            try (UnitOfWork uow2 = clientStore.beginUnitOfWork()) {
                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();
                BubbleObject inserted = Iterables.getOnlyElement(unitOfWorkTransfer.getInsertedObjects());
                assertSame(inserted, bubbleObject);
            }
        }
    }

    public void testGetTransferInsertUpdate() {
        //noinspection unused
        try (UnitOfWork uow1 = clientStore.beginUnitOfWork()) {
            Simple bubbleObject1 = new Simple();
            clientStore.insert(bubbleObject1);

            //noinspection unused
            try (UnitOfWork uow2 = clientStore.beginUnitOfWork()) {
                Simple bubbleObject2 = clientStore.get(bubbleObject1.getId());
                clientStore.update(bubbleObject2);

                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();
                BubbleObject inserted = Iterables.getOnlyElement(unitOfWorkTransfer.getInsertedObjects());
                assertSame(inserted, bubbleObject2);
                assertEquals(unitOfWorkTransfer.getUpdatedObjects(), Collections.emptyList());
            }
        }
    }

    public void testGetTransferInsertDelete() {
        //noinspection unused
        try (UnitOfWork uow1 = clientStore.beginUnitOfWork()) {
            Simple bubbleObject1 = new Simple();
            clientStore.insert(bubbleObject1);

            //noinspection unused
            try (UnitOfWork uow2 = clientStore.beginUnitOfWork()) {
                Simple bubbleObject2 = clientStore.get(bubbleObject1.getId());
                clientStore.delete(bubbleObject2);

                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();
                assertEquals(unitOfWorkTransfer.getInsertedObjects(), Collections.emptyList());
                assertEquals(unitOfWorkTransfer.getUpdatedObjects(), Collections.emptyList());
                assertEquals(unitOfWorkTransfer.getDeletedObjects(), Collections.emptyList());
            }
        }
    }

    public void testGetTransferUpdateNull() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        //noinspection unused
        try (UnitOfWork uow1 = clientStore.beginUnitOfWork()) {
            Simple bubbleObject = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            clientStore.update(bubbleObject);

            //noinspection unused
            try (UnitOfWork uow2 = clientStore.beginUnitOfWork()) {
                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();
                BubbleObject updated = Iterables.getOnlyElement(unitOfWorkTransfer.getUpdatedObjects());
                assertSame(updated, bubbleObject);
            }
        }
    }

    public void testGetTransferUpdateUpdate() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        //noinspection unused
        try (UnitOfWork uow1 = clientStore.beginUnitOfWork()) {
            Simple bubbleObject1 = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            clientStore.update(bubbleObject1);

            //noinspection unused
            try (UnitOfWork uow2 = clientStore.beginUnitOfWork()) {
                Simple bubbleObject2 = clientStore.get(bubbleObject1.getId());
                clientStore.update(bubbleObject2);

                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();
                BubbleObject updated = Iterables.getOnlyElement(unitOfWorkTransfer.getUpdatedObjects());
                assertSame(updated, bubbleObject2);
            }
        }
    }

    public void testGetTransferUpdateDelete() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        //noinspection unused
        try (UnitOfWork uow1 = clientStore.beginUnitOfWork()) {
            Simple bubbleObject1 = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            clientStore.update(bubbleObject1);

            //noinspection unused
            try (UnitOfWork uow2 = clientStore.beginUnitOfWork()) {
                Simple bubbleObject2 = clientStore.get(bubbleObject1.getId());
                clientStore.delete(bubbleObject2);

                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();
                assertEquals(unitOfWorkTransfer.getInsertedObjects(), Collections.emptyList());
                assertEquals(unitOfWorkTransfer.getUpdatedObjects(), Collections.emptyList());
                BubbleObject deleted = Iterables.getOnlyElement(unitOfWorkTransfer.getDeletedObjects());
                assertSame(deleted, bubbleObject2);
            }
        }
    }

    public void testEndUnitOfWork() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        try (UnitOfWork unitOfWork = clientStore.beginUnitOfWork()) {
            Simple simple = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            clientStore.update(simple);
            clientStore.endUnitOfWork(unitOfWork);
            fail("No exception");
        } catch (ImplementationException e) {
            assertTrue(e.getMessage().contains("Store contains modified objects"));
        }

        clientStore.evictAll();

        try (UnitOfWork unitOfWork = clientStore.beginUnitOfWork()) {
            clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            clientStore.endUnitOfWork(unitOfWork);
        }

        clientStore.evictAll();
    }

    public void testEndUnitsOfWork() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        //noinspection UnusedDeclaration
        try (UnitOfWork unitOfWork = clientStore.beginUnitOfWork()) {
            clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            Simple inserted = new Simple();
            clientStore.insert(inserted);

            //noinspection UnusedDeclaration
            try (UnitOfWork nested = clientStore.beginUnitOfWork()) {
                Simple updated = clientStore.get(mockupFacade.getSimpleMockupFactory().getSimpleId1());
                clientStore.update(updated);
                UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

                assertEquals(Iterables.getOnlyElement(unitOfWorkTransfer.getInsertedObjects()).getId(), inserted.getId());
                assertEquals(Iterables.getOnlyElement(unitOfWorkTransfer.getUpdatedObjects()).getId(), updated.getId());
                assertEquals(unitOfWorkTransfer.getDeletedObjects(), Collections.emptyList());

                clientStore.endUnitsOfWork(nested);
            }
        } finally {
            clientStore.evictAll();
        }

        assertFalse(clientStore.inUnitOfWork());
    }

    @Test(enabled = false, expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Lock on client must be done in a UnitOfWork.*")
    // TODO: SKIF-610. Midlertidig disabling av denne i påvente av GBOK-9889. Gjør klienten feiler.
    public void testClientUpdateOutsideUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        Simple simple = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
        clientStore.update(simple); // Kommer aldri her for lock kaster exceptoin
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Attempt at updating StoreSession\\(level= 2\\) with instance from lower StoreSession\\(level=1\\).*")
    public void testClientUpdateInUnitOfWorkWithObjectLockedOutsideUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
            Simple simple = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            //noinspection UnusedDeclaration
            try (UnitOfWork nested = clientStore.beginUnitOfWork()) {
                clientStore.update(simple);
            }
        }
    }

    public void testClientUpdateInUnitOfWorkWithObjectLockedOutsideUnitOfWorkDoneRight() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
            clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            //noinspection UnusedDeclaration
            try (UnitOfWork nested = clientStore.beginUnitOfWork()) {
                Simple simple = clientStore.get(mockupFacade.getSimpleMockupFactory().getSimpleId1());
                clientStore.update(simple);
                // NB: Tester her bare at oppdateringen kan utføres i UnitOfWork på klient. Hvis endringen skal lagres må den sendes som transfer til server.
            }
        }
    }

    /**
     * Tester låsing i nøstet unit-of-work, committing av unit-of-work, låsing igjen i ny nøstet unit-of-work.
     */
    public void testSKIF_555() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                try (UnitOfWork outer1 = store.beginUnitOfWork()) {
                    try (UnitOfWork inner11 = store.beginUnitOfWork()) {
                        Simple simple = store.lock(simpleId1);
                        store.update(simple);
                        store.commitUnitOfWork(inner11);
                    }
                    try (UnitOfWork inner12 = store.beginUnitOfWork()) {
                        //noinspection UnusedDeclaration
                        Simple simple = store.lock(simpleId1);
                        store.commitUnitOfWork(inner12);
                    }
                    store.commitUnitOfWork(outer1);
                }
                try (UnitOfWork outer2 = store.beginUnitOfWork()) {
                    try (UnitOfWork inner2 = store.beginUnitOfWork()) {
                        store.abortUnitOfWork(inner2);
                    }
                    store.commitUnitOfWork(outer2);
                }
                try (UnitOfWork outer3 = store.beginUnitOfWork()) {
                    try (UnitOfWork inner3 = store.beginUnitOfWork()) {
                        Simple simple = store.lock(simpleId1);
                        assertNotNull(simple);
                        store.commitUnitOfWork(inner3);
                    }
                    store.commitUnitOfWork(outer3);
                }

                return null;
            }
        });
    }

    /**
     * Tester låsing, oppdatering, ny låsing i nøstet unit-of-work, aborting av unit-of-work, låsing igjen i ny nøstet unit-of-work.
     */
    public void testSKIF_556() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                try (UnitOfWork outer = store.beginUnitOfWork()) {
                    try (UnitOfWork inner1 = store.beginUnitOfWork()) {
                        Simple simple = store.lock(simpleId1);
                        store.update(simple);
                        store.commitUnitOfWork(inner1);
                    }
                    try (UnitOfWork inner2 = store.beginUnitOfWork()) {
                        //noinspection UnusedDeclaration
                        Simple simple = store.lock(simpleId1);
                        store.abortUnitOfWork(inner2);
                    }
                    try (UnitOfWork inner3 = store.beginUnitOfWork()) {
                        Simple simple = store.lock(simpleId1);
                        assertNotNull(simple);
                        store.commitUnitOfWork(inner3);
                    }
                    store.commitUnitOfWork(outer);
                }

                return null;
            }
        });
    }

    public void testGetUnitOfWorkOnServerWhenObjectsChangedInSessionAndInNestedUnitsOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        final SimpleId<?> simpleId2 = mockupFacade.getSimpleMockupFactory().getSimpleId2();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple3UpdatedAgain;
                SimpleId<?> simpleId3 = store.getInstance(IdService.class).getNextId(SimpleId.class);
                SimpleId<?> simpleId4 = store.getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple1 = store.lock(simpleId1);
                Simple simple2 = store.lock(simpleId2);
                store.delete(simple1);
                store.update(simple2);
                Simple simple3 = new Simple(simpleId3, "Simple 3");
                store.insert(simple3);
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    assertTransferEmpty(store.getUnitOfWorkTransfer());
                    Simple simple3Updated = store.get(simpleId3);
                    simple3Updated.setText("Updated");
                    store.update(simple3Updated);
                    Simple simple4 = new Simple(simpleId4, "Simple 4");
                    store.insert(simple4);
                    assertTransfer(store.getUnitOfWorkTransfer(), ImmutableList.of(simple4), ImmutableList.of(simple3Updated), emptyList());
                    try (UnitOfWork inner = store.beginUnitOfWork()) {
                        assertTransfer(store.getUnitOfWorkTransfer(), ImmutableList.of(simple4), ImmutableList.of(simple3Updated), emptyList());
                        simple3UpdatedAgain = store.get(simpleId3);
                        simple3UpdatedAgain.setText("Updated again");
                        store.update(simple3UpdatedAgain);
                        assertTransfer(store.getUnitOfWorkTransfer(), ImmutableList.of(simple4), ImmutableList.of(simple3UpdatedAgain), emptyList());
                        store.commitUnitOfWork(inner);
                    }
                    assertTransfer(store.getUnitOfWorkTransfer(), ImmutableList.of(simple4), ImmutableList.of(simple3UpdatedAgain), emptyList());
                }
                return null;
            }
        });
    }

    public void testGetSnapshotOnClientWhenObjectsChangedInNestedUnitsOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        final SimpleId<?> simpleId2 = mockupFacade.getSimpleMockupFactory().getSimpleId2();

        try (UnitOfWork unitOfWork1 = clientStore.beginUnitOfWork()) {
            Simple simple3UpdatedAgain;
            SimpleId<?> simpleId3 = clientStore.getInstance(IdService.class).getNextId(SimpleId.class);
            SimpleId<?> simpleId4 = clientStore.getInstance(IdService.class).getNextId(SimpleId.class);
            Simple simple1 = clientStore.lock(simpleId1);
            Simple simple2 = clientStore.lock(simpleId2);
            clientStore.delete(simple1);
            clientStore.update(simple2);
            Simple simple3 = new Simple(simpleId3, "Simple 3");
            clientStore.insert(simple3);
            try (UnitOfWork unitOfWork2 = clientStore.beginUnitOfWork()) {
                assertTransfer(clientStore.getUnitOfWorkTransfer(), ImmutableList.of(simple3), ImmutableList.of(simple2), ImmutableList.of(simple1));
                Simple simple3Updated = clientStore.get(simpleId3);
                simple3Updated.setText("Updated");
                clientStore.update(simple3Updated);
                Simple simple4 = new Simple(simpleId4, "Simple 4");
                clientStore.insert(simple4);
                assertTransfer(clientStore.getUnitOfWorkTransfer(), ImmutableList.of(simple3Updated, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                try (UnitOfWork inner = clientStore.beginUnitOfWork()) {
                    assertTransfer(clientStore.getUnitOfWorkTransfer(), ImmutableList.of(simple3Updated, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                    simple3UpdatedAgain = clientStore.get(simpleId3);
                    simple3UpdatedAgain.setText("Updated again");
                    clientStore.update(simple3UpdatedAgain);
                    assertTransfer(clientStore.getUnitOfWorkTransfer(), ImmutableList.of(simple3UpdatedAgain, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                    clientStore.commitUnitOfWork(inner);
                }
                assertTransfer(clientStore.getUnitOfWorkTransfer(), ImmutableList.of(simple3UpdatedAgain, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                clientStore.commitUnitOfWork(unitOfWork2);
            }
            clientStore.abortUnitOfWork(unitOfWork1);
        }
    }

    /**
     * Tester bruk av getAllLoaded() til å hente ut original versjon av objekter som lastes og prosesseres på
     * tjeneren og overfører disse til klienten.
     */
    public void testGetAllLoaded() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        final SimpleId<?> simpleId2 = mockupFacade.getSimpleMockupFactory().getSimpleId2();

        clientStore.evictAll();
        Simple simple2 = clientStore.get(simpleId2);
        String originalText = simple2.getText();
        StoreBubbleTransfer allLoaded = clientStore.getAllLoaded();
        assertThat(allLoaded.getBubbleObjects()).hasSize(1);
        assertThat(allLoaded.getObject(simpleId2)).isSameAs(simple2);
        clientStore.evict(simpleId2);
        assertThat(clientStore.getAllLoaded().getBubbleObjects()).hasSize(0);

        // Test for Server
        StoreBubbleTransfer bubbleTransfer = (StoreBubbleTransfer) server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    store.get(simpleId1);
                    Simple simple2 = store.lock(simpleId2);
                    simple2.setText("foobar");  // Denne versjon av objektet blir ikke med
                    store.update(simple2);
                    store.insert(new Simple());  // Denne blir ikke med, det er jo et objekt som ikke lastes
                }
                return store.getAllLoaded();
            }
        });
        assertThat(bubbleTransfer.getBubbleObjects()).hasSize(2);
        assertThat(bubbleTransfer.getObject(simpleId1)).isNotNull();
        assertThat(bubbleTransfer.getObject(simpleId2)).isNotNull();
        assertThat(bubbleTransfer.getLockedIds()).hasSize(1);
        try {
            clientStore.register(bubbleTransfer);
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessage("Lock on client must be done in a UnitOfWork");
        }

        try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
            clientStore.register(bubbleTransfer);
            assertThat(clientStore.getAllLoaded().getObject(simpleId1)).isSameAs(bubbleTransfer.getObject(simpleId1));
            assertThat(clientStore.getAllLoaded().getObject(simpleId2)).isSameAs(bubbleTransfer.getObject(simpleId2));
            assertThat(clientStore.isLocked(simpleId1)).isFalse();
            assertThat(clientStore.isLocked(simpleId2)).isTrue();
            assertThat(clientStore.get(simpleId2).getText()).isNotEqualTo("foobar");
            assertThat(clientStore.get(simpleId2).getText()).isEqualTo(originalText);
        }
    }

    /**
     * Tester at et objekt som lastes i en en unit of work og som ikke modifiseres er tilgjengelig via getAllLoaded
     * både mens unit of work er aktiv og etter at unit of work er aborted. I alle tilfeller får man instansen som
     * tilsvarer objektet som ble lastet. Tester både klient og server.
     */
    public void testGetAllLoadedWhenObjectIsLoadedInAUnitOfWorkThatIsAborted() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        // Test for Klient
        testGetAllLoadedWhenObjectIsLoadedInAUnitOfWorkThatIsAborted(clientStore, simpleId);

        // Test for Server
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                testGetAllLoadedWhenObjectIsLoadedInAUnitOfWorkThatIsAborted(store, simpleId);
                return null;
            }
        });
    }

    private void testGetAllLoadedWhenObjectIsLoadedInAUnitOfWorkThatIsAborted(Store store, SimpleId<?> simpleId) {
        Simple simple1;
        Simple simple2;
        try (UnitOfWork outer = store.beginUnitOfWork()) {
            try (UnitOfWork ignore = store.beginUnitOfWork()) {
                store.get(simpleId);
                simple1 = (Simple) store.getAllLoaded().getObject(simpleId);
                store.abortUnitOfWork(ignore); // Bare for å være tydelig
            }
            simple2 = (Simple) store.getAllLoaded().getObject(simpleId);
            store.abortUnitOfWork(outer); // Bare for å være tydelig
        }
        Simple simple3 = (Simple) store.getAllLoaded().getObject(simpleId);
        assertSame(simple1, simple2, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
        assertSame(simple1, simple3, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
    }

    /**
     * Tester at et objekt som lastes og låses i en en unit of work er tilgjengelig via getAllLoaded
     * både mens unit of work er aktiv og etter at unit of work er aborted. I alle tilfeller får man instansen som
     * tilsvarer objektet som ble lastet. Objektet som store.lock() gir ut vil være en annen instans som representerer
     * den versjon av objektet som kan modifiseres. Tester både klient og server.
     */
    public void testGetAllLoadedWhenObjectIsLoadedAndLockedInAUnitOfWorkThatIsAborted() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        // Test for Klient
        testGetAllLoadedWhenObjectIsLoadedAndLockedInAUnitOfWorkThatIsAborted(clientStore, simpleId);

        // Test for Server
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                testGetAllLoadedWhenObjectIsLoadedAndLockedInAUnitOfWorkThatIsAborted(store, simpleId);
                return null;
            }
        });
    }

    private void testGetAllLoadedWhenObjectIsLoadedAndLockedInAUnitOfWorkThatIsAborted(Store store, SimpleId<?> simpleId) {
        Simple simpleLocked;
        Simple simple1;
        Simple simple2;
        try (UnitOfWork outer = store.beginUnitOfWork()) {
            try (UnitOfWork ignore = store.beginUnitOfWork()) {
                simpleLocked = store.lock(simpleId);
                simple1 = (Simple) store.getAllLoaded().getObject(simpleId);
            }
            simple2 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
            store.abortUnitOfWork(outer); // Bare for å være tydelig
        }
        Simple simple3 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
        assertNotSame(simpleLocked, simple1, "Forventet at objektet som kan modifiseres ikke er samme instans");
        assertSame(simple1, simple2, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
        assertSame(simple1, simple3, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
    }

    /**
     * Tester at et objekt som lastes, låses, og oppdateres i en en unit of work er  tilgjengelig via getAllLoaded både
     * mens unit of work er aktiv og etter at unit of work er aborted. I alle tilfeller får man instansen som
     * tilsvarer objektet som ble lastet. Objektet som store.lock() gir ut vil være en annen instans som representerer
     * den versjon av objektet som kan modifiseres. Tester både klient og server.
     */
    public void testGetAllLoadedWhenObjectIsModifiedInAUnitOfWorkThatIsAborted() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        // Test for Klient
        testGetAllLoadedWhenObjectIsModifiedInAUnitOfWorkThatIsAborted(clientStore, simpleId);

        // Test for Server
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                testGetAllLoadedWhenObjectIsModifiedInAUnitOfWorkThatIsAborted(store, simpleId);
                return null;
            }
        });
    }

    private void testGetAllLoadedWhenObjectIsModifiedInAUnitOfWorkThatIsAborted(Store store, SimpleId<?> simpleId) {
        Simple simple1;
        Simple simpleLocked;
        Simple simple2;
        try (UnitOfWork outer = store.beginUnitOfWork()) {
            try (UnitOfWork ignore = store.beginUnitOfWork()) {
                simpleLocked = store.lock(simpleId); // Her får vi en ny kopi
                store.update(simpleLocked);
                simple1 = (Simple) store.getAllLoaded().getObject(simpleId);
                store.abortUnitOfWork(ignore); // Bare for å være tydelig
            }
            simple2 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
            store.abortUnitOfWork(outer); // Bare for å være tydelig
        }
        Simple simple3 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
        assertNotSame(simpleLocked, simple1, "Forventet at objektet som kan modifiseres ikke er samme instans");
        assertSame(simple1, simple2, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
        assertSame(simple1, simple3, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
    }

    /**
     * Tester at et objekt som lastes, låses, og slettes i en en unit of work er tilgjengelig via getAllLoaded både
     * mens unit of work er aktiv og etter at unit of work er aborted. I alle tilfeller får man instansen som
     * tilsvarer objektet som ble lastet. Tester både klient og server.
     */
    public void testGetAllLoadedWhenObjectIsLoadedAndDeletedInUnitOfWorkThatIsAborted() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        // Test for Klient
        testGetAllLoadedWhenObjectIsLoadedAndDeletedInUnitOfWorkThatIsAborted(clientStore, simpleId);

        // Test for Server
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                testGetAllLoadedWhenObjectIsLoadedAndDeletedInUnitOfWorkThatIsAborted(store, simpleId);
                return null;
            }
        });
    }

    private void testGetAllLoadedWhenObjectIsLoadedAndDeletedInUnitOfWorkThatIsAborted(Store store, SimpleId<?> simpleId) {
        Simple simple1;
        Simple simpleLocked;
        Simple simple2;
        try (UnitOfWork outer = store.beginUnitOfWork()) {
            try (UnitOfWork ignore = store.beginUnitOfWork()) {
                simpleLocked = store.lock(simpleId); // Her får vi en ny kopi
                store.delete(simpleLocked);
                simple1 = (Simple) store.getAllLoaded().getObject(simpleId);
                store.abortUnitOfWork(ignore); // Bare for å være tydelig
            }
            simple2 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
            store.abortUnitOfWork(outer); // Bare for å være tydelig
        }
        Simple simple3 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
        assertNotSame(simpleLocked, simple1, "Forventet at objektet som kan modifiseres ikke er samme instans");
        assertSame(simple1, simple2, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
        assertSame(simple1, simple3, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
    }

    /**
     * Tester at et objekt som lastes, låses, og modifiseres i en en unit of work er tilgjengelig via getAllLoaded både
     * mens unit of work er aktiv og etter at unit of work er committed. Tester både klient og server.
     */
    @SuppressWarnings("Duplicates")
    public void testGetAllLoadedWhenObjectIsLockedAndModifiedInUnitOfWorkThatIsCommitted() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        // Test for Klient
        Simple simpleLocked;
        Simple simple1;
        Simple simple2;
        try (UnitOfWork outer = clientStore.beginUnitOfWork()) {
            try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
                simpleLocked = clientStore.lock(simpleId); // Her får vi en ny kopi som slettes i linjen under
                clientStore.update(simpleLocked);
                simple1 = (Simple) clientStore.getAllLoaded().getObject(simpleId); // Original versjon kan hentes ut via loaded
                clientStore.commitUnitOfWork(ignore); // Bare for å være tydelig
            }
            simple2 = (Simple) clientStore.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
            assertSame(simple1, simple2, "Forventet samme instans. Objekt skal være tilgjengelig etter abortUnitOfWork");
            clientStore.abortUnitOfWork(outer); // Vi er på klienten. Det gir ingen mening å forsøke å committe til underliggende nivå
        }

        // Test for Server
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple1;
                Simple simpleLocked;
                Simple simple2;
                try (UnitOfWork outer = store.beginUnitOfWork()) {
                    try (UnitOfWork ignore = store.beginUnitOfWork()) {
                        simpleLocked = store.lock(simpleId); // Her får vi en ny kopi som slettes i linjen under
                        store.update(simpleLocked);
                        simple1 = (Simple) store.getAllLoaded().getObject(simpleId); // Original versjon kan hentes ut via loaded
                        store.commitUnitOfWork(ignore); // Bare for å være tydelig
                    }
                    simple2 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
                    assertNotSame(simpleLocked, simple1, "Forventet at objektet som kan modifiseres ikke er samme instans");
                    assertSame(simple1, simple2, "Objekt som låses og endres skal ikke måtte lastes på nytt etter abortUnitOfWork");
                    store.commitUnitOfWork(outer);
                }

                // Her går vi mot StoreSessionServer som ikke jobber med kopier. Så det er instansen som ble modifisert som vi får ut og ikke opprinnelig!
                Simple simple3 = (Simple) store.getAllLoaded().getObject(simpleId); // Objektet er fortsatt lastet
                assertSame(simple3, simpleLocked, "Forventet at store.getAllLoaded() gir instansen som ble modifisert og ikke opprinnelig kopi");
                return null;
            }
        });
    }

    public void ytreUnitOfWorkSkalFrigiLaaserVedAbortUnitOfWork() {
        UowTestService uowTestService = clientStore.getInstance(UowTestService.class);
        releaseAllLockForCurrentUser(); // Så vi har en veldefinert tilstand
        SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        try (UnitOfWork ytre = clientStore.beginUnitOfWork()) {
            try (UnitOfWork indre = clientStore.beginUnitOfWork()) {
                StoreBubbleTransfer storeBubbleTransfer = findAndLock(simpleId);
                assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
                clientStore.register(storeBubbleTransfer);
                clientStore.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            clientStore.abortUnitOfWork(ytre);
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }


    public void indreUowSkalIkkePaavirkeBoblerEndretUOWIYtreHvisBoblenIkkeOppdateres() {
        UowTestService uowTestService = clientStore.getInstance(UowTestService.class);
        SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        assertThat(clientStore.get(simpleId).getText()).isEqualTo("foo");
        uowTestService.updateTextInNewTransaction(simpleId, "external");
        try (UnitOfWork ytre = clientStore.beginUnitOfWork()) {
            Simple simple1 = clientStore.lock(simpleId);
            assertThat(simple1.getText()).isEqualTo("external");
            simple1.setText("ytre"); // Her endres simple i ytre
            clientStore.update(simple1);
            try (UnitOfWork indre = clientStore.beginUnitOfWork()) {
                StoreBubbleTransfer storeBubbleTransfer = findAndLock(simpleId);
                Simple transferedBubble = (Simple) storeBubbleTransfer.getBubbleObjects().get(simpleId);
                assertThat(transferedBubble.getText()).isEqualTo("external"); // Server ved ikke om endring, men store gjør
                clientStore.register(storeBubbleTransfer);
                Simple simple2 = clientStore.get(simpleId);
                assertThat(simple2.getText()).isEqualTo("ytre");
                assertThat(simple2).isNotSameAs(simple1);
                simple2.setText("indre"); // Boble i indre endres, men store.update kalles ikke. Endring blir ikke med ved commit
                clientStore.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            Simple simple3 = clientStore.get(simpleId); // Boble er uforandret i ytre, fortsatt samme instans som før indre startet
            assertThat(simple3).isSameAs(simple1);
            assertThat(simple1.getText()).isEqualTo("ytre");
            clientStore.abortUnitOfWork(ytre);
        }
        assertThat(clientStore.get(simpleId).getText()).isEqualTo("external");
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }


    @Test(dataProvider = "boolean2Dmatrix")
    public void clientLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(boolean useTraferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(clientStore, simpleId, useTraferForLaasing, useLockOperation);
    }

    @Test(dataProvider = "boolean2Dmatrix")
    public void serverLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(boolean useTraferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store serverStore;

            @Override
            public Object run() {
                laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(serverStore, simpleId, useTraferForLaasing, useLockOperation);
                return null;
            }
        });
    }

    private void laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(Store store, SimpleId<?> simpleId, boolean useTraferForLaasing, boolean useLockOperation) {
        UowTestService uowTestService = store.getInstance(UowTestService.class);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        //noinspection unused
        try (UnitOfWork ytre = store.beginUnitOfWork()) {
            Simple foo = store.get(simpleId);
            uowTestService.updateTextInNewTransaction(simpleId, "external update");
            assertThat(foo).isNotNull();
            assertThat(foo.getText()).isEqualTo("foo");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            Simple fooLockedIndre;
            try (UnitOfWork indre = store.beginUnitOfWork()) {
                laasSimpleBoble(store, simpleId, useTraferForLaasing, uowTestService);
                fooLockedIndre = store.get(simpleId);
                assertThat(store.isLocked(simpleId)).isTrue();
                assertThat(fooLockedIndre).isNotSameAs(foo);
                assertThat(fooLockedIndre.getText()).isEqualTo("external update");
                // Objektet låses og oppdateres
                // Har lagt inn endring så man kan se den blir med.
                fooLockedIndre.setText("changed indre");
                store.update(fooLockedIndre);
                store.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            // Uthenting via lock og get skal ha samme effekt, da objektet allerede er låst
            Simple fooAfter = useLockOperation ? store.lock(simpleId) : store.get(simpleId);
            assertThat(fooAfter).isNotNull();
            assertThat(fooAfter).isNotSameAs(foo);
            assertThat(fooAfter).isSameAs(fooLockedIndre);
            assertThat(fooAfter.getText()).isEqualTo("changed indre");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            assertThat(store.isLocked(simpleId)).isTrue();
            assertThat(store.get(simpleId).store()).isNotNull();
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }

    @Test(dataProvider = "boolean2Dmatrix")
    public void clientLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(boolean useTraferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(clientStore, simpleId, useTraferForLaasing, useLockOperation);
    }

    @Test(dataProvider = "boolean2Dmatrix")
    public void serverLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(boolean useTraferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store serverStore;

            @Override
            public Object run() {
                laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(serverStore, simpleId, useTraferForLaasing, useLockOperation);
                return null;
            }
        });
    }

    private void laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(Store store, SimpleId<?> simpleId, boolean useTraferForLaasing, boolean useLockOperation) {
        UowTestService uowTestService = store.getInstance(UowTestService.class);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        //noinspection unused
        try (UnitOfWork ytre = store.beginUnitOfWork()) {
            Simple foo = store.get(simpleId);
            uowTestService.updateTextInNewTransaction(simpleId, "external update");
            assertThat(foo).isNotNull();
            assertThat(foo.getText()).isEqualTo("foo");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            Simple fooLockedIndre;
            try (UnitOfWork indre = store.beginUnitOfWork()) {
                laasSimpleBoble(store, simpleId, useTraferForLaasing, uowTestService);
                fooLockedIndre = store.get(simpleId);
                assertThat(store.isLocked(simpleId)).isTrue();
                assertThat(fooLockedIndre).isNotSameAs(foo);
                assertThat(fooLockedIndre.getText()).isEqualTo("external update");
                // Objektet låses men indre gjør ingen oppdatering.
                // Har lagt inn endring så man kan se den ikke blir med.
                fooLockedIndre.setText("changed indre");
                store.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            // Uthenting via lock og get skal ha samme effekt, da objektet allerede er låst
            Simple fooAfter = useLockOperation ? store.lock(simpleId) : store.get(simpleId);
            assertThat(fooAfter).isNotNull();
            assertThat(fooAfter).isNotSameAs(foo);
            assertThat(fooAfter).isNotSameAs(fooLockedIndre);
            assertThat(fooAfter.getText()).isEqualTo("external update");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            assertThat(store.isLocked(simpleId)).isTrue();
            assertThat(store.get(simpleId).store()).isNotNull();
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }


    @Test(dataProvider = "boolean1Dmatrix")
    public void clientLaasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(boolean useTraferForLaasing) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        laasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(clientStore, simpleId, useTraferForLaasing);
    }

    @Test(dataProvider = "boolean1Dmatrix")
    public void serverLaasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(boolean useTraferForLaasing) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store serverStore;

            @Override
            public Object run() {
                laasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(serverStore, simpleId, useTraferForLaasing);
                return null;
            }
        });
    }

    private void laasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(Store store, SimpleId<?> simpleId, boolean useTraferForLaasing) {
        UowTestService uowTestService = store.getInstance(UowTestService.class);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        //noinspection unused
        try (UnitOfWork ytre = store.beginUnitOfWork()) {
            Simple foo = store.get(simpleId);
            uowTestService.updateTextInNewTransaction(simpleId, "external update");
            assertThat(foo).isNotNull();
            assertThat(foo.getText()).isEqualTo("foo");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            Simple fooLockedIndre;
            try (UnitOfWork indre = store.beginUnitOfWork()) {
                laasSimpleBoble(store, simpleId, useTraferForLaasing, uowTestService);
                fooLockedIndre = store.get(simpleId);
                assertThat(store.isLocked(simpleId)).isTrue();
                assertThat(fooLockedIndre).isNotSameAs(foo);
                assertThat(fooLockedIndre.getText()).isEqualTo("external update");
                // Objektet låses men indre gjør ingen oppdatering.
                // Har lagt inn endring så man kan se den ikke blir med.
                fooLockedIndre.setText("changed indre");
                store.abortUnitOfWork(indre);
            }
            assertThat(store.isLocked(simpleId)).isFalse();
            assertThat(store.get(simpleId).getText()).isEqualTo("external update");
            assertThat(store.get(simpleId).store()).isNotNull();
        }
    }


    private void laasSimpleBoble(Store store, SimpleId<?> simpleId, boolean useTraferForLaasing, UowTestService uowTestService) {
        if (useTraferForLaasing) {
            StoreBubbleTransfer storeBubbleTransfer = uowTestService.findAndLock(simpleId);
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            store.register(storeBubbleTransfer);
        } else {
            store.lock(simpleId);
        }
    }

    private StoreBubbleTransfer findAndLock(final SimpleId<?> simpleId) {
        return (StoreBubbleTransfer) server.runInTxNotSupported(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                StoreBubbleTransfer transfer = new StoreBubbleTransfer();
                transfer.add(store.lock(simpleId));
                return transfer;
            }
        });
    }


    private void releaseAllLockForCurrentUser() {
        DBLockerService lockerService = injector.getInstance(DBLockerService.class);
        LoginUserHolder loginUser = injector.getInstance(LoginUserHolder.class);
        lockerService.releaseAllLocks(loginUser.get().getUsername());
    }

    @SuppressWarnings("SameParameterValue")
    private SimpleId<?> createSimpleObjectOnServer(final String text) {
        return (SimpleId<?>) server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple = new Simple();
                simple.setText(text);
                store.insert(simple);
                return simple.getId();
            }
        });
    }

    private void assertTransferEmpty(UnitOfWorkTransfer transfer) {
        assertTransfer(transfer, emptyList(), emptyList(), emptyList());
    }

    private void assertTransfer(UnitOfWorkTransfer transfer, List<? extends BubbleObject> insertedObjects, List<? extends BubbleObject> updatedObjects, List<? extends BubbleObject> deletedObjects) {
        assertContainsSame("inserted", transfer.getInsertedObjects(), insertedObjects);
        assertContainsSame("updated", transfer.getUpdatedObjects(), updatedObjects);
        assertContainsSame("deleted", transfer.getDeletedObjects(), deletedObjects);
    }

    private void assertContainsSame(String description, List<? extends BubbleObject> actual, List<? extends BubbleObject> expected) {
        assertThat(actual).describedAs(description).hasSize(expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat((BubbleObject) actual.get(i)).describedAs(String.format("%s[%d]", description, i)).isSameAs(expected.get(i));
        }
    }

    private static ImmutableList<BubbleObject> emptyList() {
        return ImmutableList.of();
    }
}
