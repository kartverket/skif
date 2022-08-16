package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.domain.basic.SubTypeWithCollection;
import no.statkart.skif.storetest.domain.basic.SubTypeWithCollectionId;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitive;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitiveId;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.basic.SubTypedBubbleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Tester endring av subtype på tjenersiden.
 *
 * @author Tor Egil R. Strand
 */
public class SubTypeTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService server;

    @Inject
    private Store clientStore;

    @Test(groups = {"singlevm-required"})
    public void likhet() {
        SubTypedBubbleId<?> subTypedBubbleId = new SubTypedBubbleId<>(1L);
        SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId<>(1L);
        SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId<>(1L);

        Assert.assertEquals(subTypedBubbleId, withPrimitiveId);
        Assert.assertEquals(subTypedBubbleId, withCollectionId);
        //noinspection AssertBetweenInconvertibleTypes
        Assert.assertEquals(withPrimitiveId, withCollectionId);
    }

    @Test(groups = {"singlevm-required"})
    public void enkelLesetest() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Long idValue = mockupFacade.getSubTypedBubbleMockupFactory().getDifferentHistoricSubtypesId().getValue();

                SubTypedBubbleId<?> idCurrent = new SubTypedBubbleId<>(idValue, SnapshotVersion.CURRENT);
                SubTypedBubble current = store.get(idCurrent);
                Assert.assertTrue(current instanceof SubTypeWithCollection, "Ikke SubTypeWithCollection");
                Assert.assertNotNull(((SubTypeWithCollection) current).getTekster());
                Assert.assertTrue(((SubTypeWithCollection) current).getTekster().isEmpty());

                SubTypedBubbleId<?> idPast = new SubTypedBubbleId<>(idValue, SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
                SubTypedBubble past = store.get(idPast);
                Assert.assertTrue(past instanceof SubTypeWithPrimitive, "Ikke SubTypeWithPrimitive");

                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void asSnapshotVersion() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                SubTypeWithCollectionId<?> idCurrent = mockupFacade.getSubTypedBubbleMockupFactory().getDifferentHistoricSubtypesId();
                SubTypedBubble current = store.get(idCurrent);
                Assert.assertTrue(current instanceof SubTypeWithCollection, "Ikke SubTypeWithCollection");

                SubTypedBubbleId<?> idPast = (SubTypedBubbleId<?>) idCurrent.asSnapshotVersion(SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
                SubTypedBubble past = store.get(idPast);
                Assert.assertTrue(past instanceof SubTypeWithPrimitive, "Ikke SubTypeWithPrimitive");

                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void insertPlusUpdateWithTypeChange() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        final long idValue = (Long) idService.getNextIdValue(SubTypeWithPrimitiveId.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                SubTypeWithPrimitiveId<?> id = new SubTypeWithPrimitiveId<>(idValue);

                SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
                subTypeWithPrimitive.setId(id);
                subTypeWithPrimitive.setNum(9);
                subTypeWithPrimitive.setText("Inserted");
                store.insert(subTypeWithPrimitive);
                assertThat(store.getInsertedIds()).extracting("value", "class")
                        .as("has final subtype")
                        .containsExactly(tuple(idValue, SubTypeWithPrimitiveId.class));
                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId<>(idValue);
                SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId<>(idValue);

                store.lock(withPrimitiveId);

                SubTypeWithCollection subTypeWithCollection = new SubTypeWithCollection();
                subTypeWithCollection.setId(withCollectionId);
                subTypeWithCollection.setText("Updated");
                store.update(subTypeWithCollection);
                assertThat(store.getUpdatedIds()).extracting("value", "class")
                        .as("has final subtype")
                        .containsExactly(tuple(idValue, SubTypeWithCollectionId.class));

                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Inject
            private Provider<Connection> connectionProvider;

            @Override
            public Object run() {
                SubTypedBubbleId<?> id = new SubTypedBubbleId<>(idValue);

                SubTypedBubble subTypedBubble = store.get(id);
                Assert.assertTrue(subTypedBubble instanceof SubTypeWithCollection, "Boblen endret ikke type og er fortsatt " + subTypedBubble.getClass());

                Connection connection = connectionProvider.get();
                try (
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("select num from subtypedbubble where id=" + idValue)
                ) {

                    Assert.assertTrue(resultSet.next(), "Fant ingen rader");
                    Assert.assertNull(resultSet.getObject(1), "num er ikke nullet ut");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void insertPlusUpdateWithTypeChange2() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        final long idValue = (Long) idService.getNextIdValue(SubTypeWithPrimitiveId.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                SubTypeWithCollectionId<?> id = new SubTypeWithCollectionId<>(idValue);

                SubTypeWithCollection subTypeWithCollection = new SubTypeWithCollection();
                subTypeWithCollection.setId(id);
                subTypeWithCollection.getTekster().add("Hoppsann");
                subTypeWithCollection.setText("Inserted");
                store.insert(subTypeWithCollection);
                assertThat(store.getInsertedIds()).extracting("value", "class")
                        .as("has final subtype")
                        .containsExactly(tuple(idValue, SubTypeWithCollectionId.class));

                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId<>(idValue);
                SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId<>(idValue);

                store.lock(withCollectionId);

                SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
                subTypeWithPrimitive.setId(withPrimitiveId);
                subTypeWithPrimitive.setText("Updated");
                subTypeWithPrimitive.setNum(8);
                store.update(subTypeWithPrimitive);
                assertThat(store.getUpdatedIds()).extracting("value", "class")
                        .as("has final subtype")
                        .containsExactly(tuple(idValue, SubTypeWithPrimitiveId.class));

                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Inject
            private Provider<Connection> connectionProvider;

            @Override
            public Object run() {
                SubTypedBubbleId<?> id = new SubTypedBubbleId<>(idValue);

                SubTypedBubble subTypedBubble = store.get(id);
                Assert.assertTrue(subTypedBubble instanceof SubTypeWithPrimitive, "Boblen endret ikke type og er fortsatt " + subTypedBubble.getClass());

                Connection connection = connectionProvider.get();
                try (
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("select * from TekstForSubtype where subtypedid=" + idValue)
                ) {

                    Assert.assertFalse(resultSet.next(), "Fant rader");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void insertPlusUpdateWithTypeChangeNoLockOnServer() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        final long idValue = (Long) idService.getNextIdValue(SubTypeWithPrimitiveId.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                SubTypeWithPrimitiveId<?> id = new SubTypeWithPrimitiveId<>(idValue);

                SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
                subTypeWithPrimitive.setId(id);
                subTypeWithPrimitive.setNum(9);
                subTypeWithPrimitive.setText("Inserted");
                store.insert(subTypeWithPrimitive);
                assertThat(store.getInsertedIds()).extracting("value", "class")
                        .as("has final subtype")
                        .containsExactly(tuple(idValue, SubTypeWithPrimitiveId.class));

                return null;
            }
        });

        SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId<>(idValue);
        SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId<>(idValue);

        try (UnitOfWork unitOfWork = clientStore.beginUnitOfWork()) {
            clientStore.lock(withPrimitiveId);

            server.run(new RunOnServerMethod() {
                @Inject
                private StoreServer store;

                @Override
                public Object run() {
                    SubTypeWithCollection subTypeWithCollection = new SubTypeWithCollection();
                    subTypeWithCollection.setId(withCollectionId);
                    subTypeWithCollection.setText("Updated");

                    store.update(subTypeWithCollection);
                    assertThat(store.getUpdatedIds()).extracting("value", "class")
                            .as("has final subtype")
                            .containsExactly(tuple(idValue, SubTypeWithCollectionId.class));

                    return null;
                }
            });

            clientStore.endUnitOfWork(unitOfWork);
        }

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Inject
            private Provider<Connection> connectionProvider;

            @Override
            public Object run() {
                SubTypedBubbleId<?> id = new SubTypedBubbleId<>(idValue);

                SubTypedBubble subTypedBubble = store.get(id);
                Assert.assertTrue(subTypedBubble instanceof SubTypeWithCollection, "Boblen endret ikke type og er fortsatt " + subTypedBubble.getClass());

                Connection connection = connectionProvider.get();
                try (
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("select num from subtypedbubble where id=" + idValue)
                ) {

                    Assert.assertTrue(resultSet.next(), "Fant ingen rader");
                    Assert.assertNull(resultSet.getObject(1), "num er ikke nullet ut");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void insertPlusUpdateWithTypeChangeViaUnitOfWorkTransfer() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        final long idValue = (Long) idService.getNextIdValue(SubTypeWithPrimitiveId.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                SubTypeWithPrimitiveId<?> id = new SubTypeWithPrimitiveId<>(idValue);

                SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
                subTypeWithPrimitive.setId(id);
                subTypeWithPrimitive.setNum(9);
                subTypeWithPrimitive.setText("Inserted");
                store.insert(subTypeWithPrimitive);
                assertThat(store.getInsertedIds()).extracting("value", "class")
                        .as("has final subtype")
                        .containsExactly(tuple(idValue, SubTypeWithPrimitiveId.class));
                return null;
            }
        });

        {
            Store store = injector.getInstance(Store.class);

            try (UnitOfWork unitOfWork = store.beginUnitOfWork()) {
                SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId<>(idValue);
                SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId<>(idValue);

                store.lock(withPrimitiveId);

                SubTypeWithCollection subTypeWithCollection = new SubTypeWithCollection();
                subTypeWithCollection.setId(withCollectionId);
                subTypeWithCollection.setText("Updated");
                store.update(subTypeWithCollection);

                UnitOfWorkTransfer unitOfWorkTransfer = store.getUnitOfWorkTransfer();

                server.run(new RunOnServerMethod() {
                    @Inject
                    private StoreServer store;

                    @Override
                    public Object run() {
                        store.registerTransfer(unitOfWorkTransfer);
                        assertThat(store.getUpdatedIds()).extracting("value", "class")
                                .as("has final subtype")
                                .containsExactly(tuple(idValue, SubTypeWithCollectionId.class));

                        return null;
                    }
                });

                store.endUnitOfWork(unitOfWork);
            }
        }

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Inject
            private Provider<Connection> connectionProvider;

            @Override
            public Object run() {
                SubTypedBubbleId<?> id = new SubTypedBubbleId<>(idValue);

                SubTypedBubble subTypedBubble = store.get(id);
                Assert.assertTrue(subTypedBubble instanceof SubTypeWithCollection, "Boblen endret ikke type og er fortsatt " + subTypedBubble.getClass());

                Connection connection = connectionProvider.get();
                try (
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("select num from subtypedbubble where id=" + idValue)
                ) {

                    Assert.assertTrue(resultSet.next(), "Fant ingen rader");
                    Assert.assertNull(resultSet.getObject(1), "num er ikke nullet ut");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
    }
}
