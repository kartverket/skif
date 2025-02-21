package no.statkart.skif.storetest.historikk;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * @author Roar Ingebrigtsen
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class SnapshotTest extends StoreTestMixedTestCase {
    @Inject
    private StoreService storeService;

    public void testHentObjectForForskjelligSnapshot() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionManager sessionManager;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

                HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
                HistSimple HistSimpleCurrent = sessionManager.get(histSimpleId1);

                SnapshotVersion historiskSnapshot = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                HistSimple HistSimpleHistorisk = sessionManager.get(histSimpleId1.asSnapshotVersion(historiskSnapshot));

                HistSimple HistSimpleOld =  sessionManager.get(histSimpleId1.asSnapshotVersion(SnapshotVersion.OLD));

                assertEquals(HistSimpleCurrent.getText(), "KARTVEIEN");
                assertEquals(HistSimpleHistorisk.getText(), "KARTVEGEN");
                assertEquals(HistSimpleOld.getText(), "KARTVEIEN");

                return null;
            }
        });
    }

    public void testHenterSammeObjectInstansForForskjelligSnapshotInstanserMedSammeVerdi() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionManager sessionManager;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

                HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
                SnapshotVersion historiskSnapshot = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                HistSimple histSimpleHistorisk = sessionManager.get(histSimpleId1.asSnapshotVersion(historiskSnapshot));
                SnapshotVersion historiskSnapshot2 = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                assertThat(historiskSnapshot).isEqualTo(historiskSnapshot2);
                assertThat(historiskSnapshot).isNotSameAs(historiskSnapshot2);
                HistSimple histSimpleHistorisk2 = sessionManager.get(histSimpleId1.asSnapshotVersion(historiskSnapshot2));
                assertThat(histSimpleHistorisk).isSameAs(histSimpleHistorisk2);
                return null;
            }
        });
    }

    public void testSommertidVintertid() {
        StoreTestMockupFacadeFactory mockupFacadeFactory = injector.getInstance(StoreTestMockupFacadeFactory.class);
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
        final HistSimpleId<?> id = mockupFacade.getIdService().getNextId(HistSimpleId.class);

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                Session session = store.getInstance(Session.class);
                session.doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                            try (PreparedStatement statement = connection.prepareStatement("insert into snapshot_trans values(TO_TIMESTAMP_TZ('2015-06-01 12:00:00.00+02', 'YYYY-MM-DD HH24:MI:SS.FFTZH'))")) {
                            statement.executeUpdate();
                        }
                    }
                });

                HistSimple histSimple = new HistSimple(id);
                histSimple.setNr(1);
                histSimple.setText("Sommer");
                store.insert(histSimple);

                return null;
            }
        });

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                Session session = store.getInstance(Session.class);
                session.doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        try (PreparedStatement statement = connection.prepareStatement("insert into snapshot_trans values(TO_TIMESTAMP_TZ('2015-10-25 02:45:00.00+02', 'YYYY-MM-DD HH24:MI:SS.FFTZH'))")) {
                            statement.executeUpdate();
                        }
                    }
                });

                HistSimple histSimple = store.lock(id);
                histSimple.setText("Høst");
                store.update(histSimple);

                return null;
            }
        });

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                Session session = store.getInstance(Session.class);
                session.doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        try (PreparedStatement statement = connection.prepareStatement("insert into snapshot_trans values(TO_TIMESTAMP_TZ('2015-10-25 02:15:00.00+01', 'YYYY-MM-DD HH24:MI:SS.FFTZH'))")) {
                            statement.executeUpdate();
                        }
                    }
                });

                HistSimple histSimple = store.lock(id);
                histSimple.setText("Vinter");
                store.update(histSimple);

                return null;
            }
        });

        List<? extends HistSimpleId<?>> versions = storeService.getVersions(id, SnapshotVersion.START, SnapshotVersion.CURRENT);
//        assertEquals(versions.size(), 3, "Forventet 3 versjoner i historikk");

        storeService.getObject(id.asSnapshotVersion(SnapshotVersion.createInstance("2015-10-25 02:30:00.00")));
    }

}
