package no.statkart.skif.storetest2.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest2.domain.subtype.*;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import no.statkart.skif.util.JDBCHelper;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tester endring av subtype på tjenersiden.
 *
 * @author Tor Egil R. Strand
 */
@Test(groups = "singlevm-required")
public class SubTypeTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService server;

    public void likhet() {
        SubTypedBubbleId<?> subTypedBubbleId = new SubTypedBubbleId(1L);
        SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId(1L);
        SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId(1L);

        Assert.assertEquals(subTypedBubbleId, withPrimitiveId);
        Assert.assertEquals(subTypedBubbleId, withCollectionId);
        Assert.assertEquals(withPrimitiveId, withCollectionId);
    }

    public void enkelLesetest() {
        final StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Long idValue = mockupFacade.getSubTypedBubbleMockupFactory().getDifferentHistoricSubtypesId().getValue();

                SubTypedBubbleId<?> idCurrent = new SubTypedBubbleId(idValue, SnapshotVersion.CURRENT);
                SubTypedBubble current = store.get(idCurrent);
                Assert.assertTrue("Ikke SubTypeWithCollection", current instanceof SubTypeWithCollection);

                SubTypedBubbleId<?> idPast = new SubTypedBubbleId(idValue, SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
                SubTypedBubble past = store.get(idPast);
                Assert.assertTrue("Ikke SubTypeWithPrimitive", past instanceof SubTypeWithPrimitive);

                return null;
            }
        });
    }

    public void asSnapshotVersion() {
        final StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                SubTypeWithCollectionId<?> idCurrent = mockupFacade.getSubTypedBubbleMockupFactory().getDifferentHistoricSubtypesId();
                SubTypedBubble current = store.get(idCurrent);
                Assert.assertTrue("Ikke SubTypeWithCollection", current instanceof SubTypeWithCollection);

                SubTypedBubbleId<?> idPast = (SubTypedBubbleId) idCurrent.asSnapshotVersion(SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
                SubTypedBubble past = store.get(idPast);
                Assert.assertTrue("Ikke SubTypeWithPrimitive", past instanceof SubTypeWithPrimitive);

                return null;
            }
        });
    }

    public void insertPlusUpdateWithTypeChange() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        final long idValue = (Long) idService.getNextIdValue(SubTypeWithPrimitiveId.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                SubTypeWithPrimitiveId<?> id = new SubTypeWithPrimitiveId(idValue);

                SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
                subTypeWithPrimitive.setId(id);
                subTypeWithPrimitive.setNum(9);
                subTypeWithPrimitive.setText("Inserted");
                store.insert(subTypeWithPrimitive);

                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId(idValue);
                SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId(idValue);

                store.lock(withPrimitiveId);

                SubTypeWithCollection subTypeWithCollection = new SubTypeWithCollection();
                subTypeWithCollection.setId(withCollectionId);
                subTypeWithCollection.setText("Updated");
                store.update(subTypeWithCollection);

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
                SubTypedBubbleId<?> id = new SubTypedBubbleId(idValue);

                SubTypedBubble subTypedBubble = store.get(id);
                Assert.assertTrue("Boblen endret ikke type og er fortsatt " + subTypedBubble.getClass(), subTypedBubble instanceof SubTypeWithCollection);

                Connection connection = connectionProvider.get();
                Statement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery("select num from subtypedbubble where id=" + idValue);

                    Assert.assertTrue("Fant ingen rader", resultSet.next());
                    Assert.assertNull("num er ikke nullet ut", resultSet.getObject(1));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    JDBCHelper.close(resultSet, statement);
                }

                return null;
            }
        });
    }

    public void insertPlusUpdateWithTypeChange2() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        final long idValue = (Long) idService.getNextIdValue(SubTypeWithPrimitiveId.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                SubTypeWithCollectionId<?> id = new SubTypeWithCollectionId(idValue);

                SubTypeWithCollection subTypeWithCollection = new SubTypeWithCollection();
                subTypeWithCollection.setId(id);
                subTypeWithCollection.getTekster().add("Hoppsann");
                subTypeWithCollection.setText("Inserted");
                store.insert(subTypeWithCollection);

                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                SubTypeWithCollectionId<?> withCollectionId = new SubTypeWithCollectionId(idValue);
                SubTypeWithPrimitiveId<?> withPrimitiveId = new SubTypeWithPrimitiveId(idValue);

                store.lock(withCollectionId);

                SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
                subTypeWithPrimitive.setId(withPrimitiveId);
                subTypeWithPrimitive.setText("Updated");
                subTypeWithPrimitive.setNum(8);
                store.update(subTypeWithPrimitive);

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
                SubTypedBubbleId<?> id = new SubTypedBubbleId<SubTypedBubble>(idValue);

                SubTypedBubble subTypedBubble = store.get(id);
                Assert.assertTrue("Boblen endret ikke type og er fortsatt " + subTypedBubble.getClass(), subTypedBubble instanceof SubTypeWithPrimitive);

                Connection connection = connectionProvider.get();
                Statement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery("select * from TekstForSubtype where subtypedid=" + idValue);

                    Assert.assertFalse("Fant rader", resultSet.next());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    JDBCHelper.close(resultSet, statement);
                }

                return null;
            }
        });
    }
}
