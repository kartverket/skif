package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Tester uthenting av snapshot på klient og server bruk av UnitOfWork på server.
 *
 * @author Henrik Fredholm
 * @since 2.8
 */
@Test(groups = "singlevm-required")
public class StoreSnapshotTest extends StoreTestMixedTestCase {
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
                        mockupFacade.getSimpleMockupFactory().getSimpleId2());
            }
        });
    }

    public void getSnapshotOnServerWhenNoChanges() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;

            public Object run() {
                assertTransferEmpty(store.getSnapshot());
                assertTransferEmpty(store.getSessionSnapshot());
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                assertTransferEmpty(store.getSnapshot());
                assertTransferEmpty(store.getSessionSnapshot());
                store.commitUnitOfWork(unitOfWork);
                assertTransferEmpty(store.getSnapshot());
                assertTransferEmpty(store.getSessionSnapshot());
                return null;
            }
        });
    }

    public void getSnapshotOnClientWhenUnitOfWorkActive() {
        try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
            assertTransferEmpty(clientStore.getSnapshot());
            assertTransferEmpty(clientStore.getSessionSnapshot());
        }
    }

    public void getSnapshotOnClientWhenNoUnitOfWorkActive() {
        try {
            clientStore.getSnapshot();
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessage("Not in UnitOfWork");
        }

        try {
            clientStore.getSessionSnapshot();
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessage("Not in UnitOfWork");
        }
    }

    public void getSnapshotOnServerWhenInsertInSessionAndNoChangeInUnitOfWork() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                assertTransfer(store.getSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, ImmutableList.of(simple), emptyList(), emptyList());
                    assertTransferEmpty(store.getSessionSnapshot());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenUpdateInSessionAndNoChangeInUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple = store.lock(simpleId);
                store.update(simple);
                assertTransfer(store.getSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, emptyList(), ImmutableList.of(simple), emptyList());
                    assertTransferEmpty(store.getSessionSnapshot());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenDeleteInSessionAndNoChangeInUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple = store.lock(simpleId);
                store.delete(simple);
                assertTransfer(store.getSnapshot(), emptyList(), emptyList(), ImmutableList.of(simple));
                assertTransfer(store.getSessionSnapshot(), emptyList(), emptyList(), ImmutableList.of(simple));
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, emptyList(), emptyList(), ImmutableList.of(simple));
                    assertTransferEmpty(store.getSessionSnapshot());
                }
                return null;
            }
        });
    }


    public void getSnapshotOnServerWhenInsertInSessionAndUpdateInUnitOfWork() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                assertTransfer(store.getSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    assertTransfer(store.getSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                    assertTransferEmpty(store.getSessionSnapshot());
                    Simple simpleUpdated = store.get(simpleId);
                    simpleUpdated.setText("changed");
                    store.update(simpleUpdated);
                    assertTransfer(store.getSnapshot(), ImmutableList.of(simpleUpdated), emptyList(), emptyList());
                    assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simpleUpdated), emptyList());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenInsertInSessionAndDeleteInUnitOfWork() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                Simple simple = new Simple(simpleId, "Simple 101");
                store.insert(simple);
                assertTransfer(store.getSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, ImmutableList.of(simple), emptyList(), emptyList());
                    assertTransferEmpty(store.getSessionSnapshot());
                    Simple simpleDeleted = store.get(simpleId);
                    simpleDeleted.setText("deleted");
                    store.delete(simpleDeleted);
                    assertTransferEmpty(store.getSnapshot());
                    assertTransfer(store.getSessionSnapshot(), emptyList(), emptyList(), ImmutableList.of(simpleDeleted));
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenUpdateInSessionAndUpdateInUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple = store.lock(simpleId);
                store.update(simple);
                assertTransfer(store.getSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, emptyList(), ImmutableList.of(simple), emptyList());
                    assertTransferEmpty(store.getSessionSnapshot());
                    Simple simpleUpdated = store.get(simpleId);
                    simpleUpdated.setText("updated");
                    store.update(simpleUpdated);
                    assertTransfer(store.getSnapshot(), emptyList(), ImmutableList.of(simpleUpdated), emptyList());
                    assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simpleUpdated), emptyList());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenUpdateInSessionAndDeleteInUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple = store.lock(simpleId);
                store.update(simple);
                assertTransfer(store.getSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, emptyList(), ImmutableList.of(simple), emptyList());
                    assertTransferEmpty(store.getSessionSnapshot());
                    Simple simpleDeleted = store.get(simpleId);
                    simpleDeleted.setText("deleted");
                    store.delete(simpleDeleted);
                    assertTransfer(store.getSnapshot(), emptyList(), emptyList(), ImmutableList.of(simpleDeleted));
                    assertTransfer(store.getSessionSnapshot(), emptyList(), emptyList(), ImmutableList.of(simpleDeleted));
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenInsertObjectInUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                    Simple simple = new Simple(simpleId, "Simple 101");
                    store.insert(simple);
                    assertTransfer(store.getSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                    assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenInsertDeleteObjectInSameUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                    Simple simple = new Simple(simpleId, "Simple 101");
                    store.insert(simple);
                    store.delete(simple);
                    assertTransferEmpty(store.getSnapshot());
                    assertTransferEmpty(store.getSessionSnapshot());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenInsertUpdateObjectInSameUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    SimpleId<?> simpleId = mockupFacade.getStore().getInstance(IdService.class).getNextId(SimpleId.class);
                    Simple simple = new Simple(simpleId, "Simple 101");
                    store.insert(simple);
                    store.update(simple);
                    assertTransfer(store.getSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                    assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple), emptyList(), emptyList());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenUpdatedObjectInUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    Simple simple = store.lock(simpleId);
                    simple.setText("changed");
                    store.update(simple);
                    assertTransfer(store.getSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                    assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simple), emptyList());
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenDeletedObjectInUnitOfWork() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final SimpleId<?> simpleId = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                try (UnitOfWork ignore = store.beginUnitOfWork()) {
                    Simple simple = store.lock(simpleId);
                    simple.setText("deleted");
                    store.delete(simple);
                    assertTransfer(store.getSnapshot(), emptyList(), emptyList(), ImmutableList.of(simple));
                    assertTransfer(store.getSessionSnapshot(), emptyList(), emptyList(), ImmutableList.of(simple));
                }
                return null;
            }
        });
    }

    public void getSnapshotOnServerWhenObjectsChangedInSessionAndInNestedUnitsOfWork() {
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
                    Simple simple3Updated = store.get(simpleId3);
                    simple3Updated.setText("Updated");
                    store.update(simple3Updated);
                    Simple simple4 = new Simple(simpleId4, "Simple 4");
                    store.insert(simple4);
                    assertTransfer(store.getSnapshot(), ImmutableList.of(simple3Updated, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                    assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple4), ImmutableList.of(simple3Updated), emptyList());
                    try (UnitOfWork inner = store.beginUnitOfWork()) {
                        assertTransfer(store.getSnapshot(), ImmutableList.of(simple3Updated, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                        assertTransferEmpty(store.getSessionSnapshot());
                        simple3UpdatedAgain = store.get(simpleId3);
                        simple3UpdatedAgain.setText("Updated again");
                        store.update(simple3UpdatedAgain);
                        assertTransfer(store.getSnapshot(), ImmutableList.of(simple3UpdatedAgain, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                        assertTransfer(store.getSessionSnapshot(), emptyList(), ImmutableList.of(simple3UpdatedAgain), emptyList());
                        store.commitUnitOfWork(inner);
                    }
                    UnitOfWorkTransfer snapshot = store.getSnapshot();
                    assertTransfer(snapshot, ImmutableList.of(simple3UpdatedAgain, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                    assertTransfer(store.getSessionSnapshot(), ImmutableList.of(simple4), ImmutableList.of(simple3UpdatedAgain), emptyList());
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
                Simple simple3Updated = clientStore.get(simpleId3);
                simple3Updated.setText("Updated");
                clientStore.update(simple3Updated);
                Simple simple4 = new Simple(simpleId4, "Simple 4");
                clientStore.insert(simple4);
                assertTransfer(clientStore.getSnapshot(), ImmutableList.of(simple3Updated, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                assertTransfer(clientStore.getSessionSnapshot(), ImmutableList.of(simple4), ImmutableList.of(simple3Updated), emptyList());
                try (UnitOfWork unitOfWork3 = clientStore.beginUnitOfWork()) {
                    assertTransfer(clientStore.getSnapshot(), ImmutableList.of(simple3Updated, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                    assertTransferEmpty(clientStore.getSessionSnapshot());
                    simple3UpdatedAgain = clientStore.get(simpleId3);
                    simple3UpdatedAgain.setText("Updated again");
                    clientStore.update(simple3UpdatedAgain);
                    assertTransfer(clientStore.getSnapshot(), ImmutableList.of(simple3UpdatedAgain, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                    assertTransfer(clientStore.getSessionSnapshot(), emptyList(), ImmutableList.of(simple3UpdatedAgain), emptyList());
                    clientStore.commitUnitOfWork(unitOfWork3);
                }
                UnitOfWorkTransfer snapshot = clientStore.getSnapshot();
                assertTransfer(snapshot, ImmutableList.of(simple3UpdatedAgain, simple4), ImmutableList.of(simple2), ImmutableList.of(simple1));
                assertTransfer(clientStore.getSessionSnapshot(), ImmutableList.of(simple4), ImmutableList.of(simple3UpdatedAgain), emptyList());
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
