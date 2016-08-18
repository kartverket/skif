package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.assertNotFound;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tester bruk av UnitOfWork på server. Alle tester kjøres via bean managed transaction slik at ingen ting blir
 * committet til databasen.
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

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return ImmutableSet.of(
                        mockupFacade.getSimpleMockupFactory().getSimpleId1(),
                        mockupFacade.getSimpleMockupFactory().getSimpleId2()
                        );
            }
        });
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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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
                assertSame(store.get(simpleId), copy);
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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                store.beginUnitOfWork();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                store.beginUnitOfWork();
                Simple copy = simple; //CopyHelper.copy(simple); // Bruker feil instans her
                store.delete(copy);
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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

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

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Update on client must be done in a UnitOfWork and sent to server via getUnitOfWorkTransfer.*")
    public void testClientUpdateOutsideUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        Simple simple = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
        clientStore.update(simple);
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Attempt at updating StoreSession\\(level= 1\\) with instance from lower StoreSession\\(level=0\\).*")
    public void testClientUpdateInUnitOfWorkWithObjectLockedOutsideUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        Simple simple = clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
        //noinspection UnusedDeclaration
        try (UnitOfWork nested = clientStore.beginUnitOfWork()) {
            clientStore.update(simple);
        }
    }

    public void testClientUpdateInUnitOfWorkWithObjectLockedOutsideUnitOfWorkDoneRight() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        clientStore.lock(mockupFacade.getSimpleMockupFactory().getSimpleId1());
        //noinspection UnusedDeclaration
        try (UnitOfWork nested = clientStore.beginUnitOfWork()) {
            Simple simple = clientStore.get(mockupFacade.getSimpleMockupFactory().getSimpleId1());
            clientStore.update(simple);
            // NB: Tester her bare at oppdateringen kan utføres i UnitOfWork på klient. Hvis endringen skal lagres må den sendes som transfer til server.
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

    public void getUnitOfWorkOnServerWhenObjectsChangedInSessionAndInNestedUnitsOfWork() {
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

    public void getSnapshotOnClientWhenObjectsChangedInNestedUnitsOfWork() {
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
            assertThat(actual.get(i)).describedAs(String.format("%s[%d]", description, i)).isSameAs(expected.get(i));
        }
    }

    private static ImmutableList<BubbleObject> emptyList() {
        return ImmutableList.of();
    }
}
