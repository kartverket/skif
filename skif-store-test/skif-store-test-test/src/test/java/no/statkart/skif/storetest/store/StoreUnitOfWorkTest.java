package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
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
import java.util.Set;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.assertNotFound;
import static org.testng.Assert.*;

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
}
