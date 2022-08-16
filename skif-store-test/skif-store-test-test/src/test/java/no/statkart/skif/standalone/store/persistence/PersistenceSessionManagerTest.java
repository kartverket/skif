package no.statkart.skif.standalone.store.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistory;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistoryId;
import org.hibernate.Session;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.CURRENT;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.OLD;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.S3;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.S4;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;

/**
 * Tester for PersistenceSessionManager
 * <p>
 * Dette er en stand-alone-test som g�r direkte mot databasen uten � bruke StoreTestServer modulen. Mest naturlig at testene
 * kj�res i singleVM mode.
 * @author Henrik Fredholm
 */
@Test(groups = "singlevm-required")
public class PersistenceSessionManagerTest {
    Properties hibernateProperties;

    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_CURRENT = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, CURRENT);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_OLD = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, OLD);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_S3 = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, S3);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_S4 = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, S4);

    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_CURRENT = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, CURRENT);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_S3 = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, S3);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_S4 = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, S4);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_OLD = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, OLD);

    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public PersistenceSessionManagerTest() {
        hibernateProperties = StandAloneTestHelper.createHibernatePropertiesSingleVm();
    }

    @BeforeClass
    public void setUp() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithHistory();
        sessionFactoryManagerBundle = createHibernateSessionFactorManagerBundle(sessionFactoryBuilder, hibernateProperties);
    }

    @AfterClass
    void tearDown() {
        sessionFactoryManagerBundle.close();
    }

    private PersistenceSessionManager createPersistenceSessionManager() {
        return new
                DefaultPersistenceSessionManager(
                    new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0)),
                    new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1))
            );
    }

    public void testLoadObjectsForMultipleSnapshotVersions() {
        PersistenceSessionManager persistenceSession = createPersistenceSessionManager();

        try {
            TestBubbleWithHistory TestBubbleWithHistory_10_CURRENT = persistenceSession.get(TestBubbleWithHistoryId_10_CURRENT);
            assertThat(TestBubbleWithHistory_10_CURRENT.getId().getSnapshotVersion()).isEqualTo(CURRENT);

            TestBubbleWithHistory TestBubbleWithHistory_10_OLD = persistenceSession.get(TestBubbleWithHistoryId_10_OLD);
            assertThat(TestBubbleWithHistory_10_OLD.getId().getSnapshotVersion()).isEqualTo(OLD);


            TestBubbleWithHistory TestBubbleWithHistory_10_S3 = persistenceSession.get(TestBubbleWithHistoryId_10_S3);
            assertThat(TestBubbleWithHistory_10_S3.getId().getSnapshotVersion()).isEqualTo(S3);

            try {
                Session session = persistenceSession.getForSnapshotVersion(OLD).getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
                TestBubbleWithHistory TestBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_OLD);
                assertThat(TestBubbleWithHistory_11_OLD.getId().getSnapshotVersion()).isEqualTo(OLD);
            } finally {
                persistenceSession.getForSnapshotVersion(OLD).getImplementation(HibernatePersistenceSessionMaster.class).releaseSession();
            }
            TestBubbleWithHistory TestBubbleWithHistory_11_S3 = persistenceSession.get(TestBubbleWithHistoryId_11_S3);
            assertThat(TestBubbleWithHistory_11_S3.getId().getSnapshotVersion()).isEqualTo(S3);

            TestBubbleWithHistory TestBubbleWithHistory_11_CURRENT = persistenceSession.get(TestBubbleWithHistoryId_11_CURRENT);
            assertThat(TestBubbleWithHistory_11_CURRENT.getId().getSnapshotVersion()).isEqualTo(CURRENT);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForMCollectionWithultipleSnapshotVersions() {
        PersistenceSessionManager persistenceSession = createPersistenceSessionManager();

        try {
            List<TestBubbleWithHistoryId<TestBubbleWithHistory>> ids = new ArrayList<TestBubbleWithHistoryId<TestBubbleWithHistory>>();
            ids.add(TestBubbleWithHistoryId_10_CURRENT);
            ids.add(TestBubbleWithHistoryId_10_OLD);
            ids.add(TestBubbleWithHistoryId_11_OLD);
            ids.add(TestBubbleWithHistoryId_11_CURRENT);
            ids.add(TestBubbleWithHistoryId_10_S3);
            ids.add(TestBubbleWithHistoryId_10_S4);
            ids.add(TestBubbleWithHistoryId_11_S4);
            Collection<? extends TestBubbleWithHistory> TestBubbleWithHistorys = persistenceSession.get(ids);
            assertThat(extractProperty("id").from(TestBubbleWithHistorys)).containsOnly(TestBubbleWithHistoryId_10_CURRENT, TestBubbleWithHistoryId_10_OLD, TestBubbleWithHistoryId_11_OLD, TestBubbleWithHistoryId_11_CURRENT, TestBubbleWithHistoryId_10_S3, TestBubbleWithHistoryId_10_S4, TestBubbleWithHistoryId_11_S4);
        } finally {
            persistenceSession.close();
        }
    }


    @Test(invocationCount = 200)
    public void testLoadObjectWithHistory_many() {
        testLoadObjectsForMultipleSnapshotVersions();
    }


    public void testLoadObjectsForOLD() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            TestBubbleWithHistory TestBubbleWithHistory_10_OLD = persistenceSession.get(TestBubbleWithHistoryId_10_OLD);
            assertThat(TestBubbleWithHistory_10_OLD.getId().getSnapshotVersion()).isEqualTo(OLD);

            TestBubbleWithHistory TestBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_OLD);
            assertThat(TestBubbleWithHistory_11_OLD.getId().getSnapshotVersion()).isEqualTo(OLD);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForHistoric() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            // M� endre snapshot version fra OLD til S3 siden OLD er default for valgt persistence session
            persistenceSession.setSnapshot(S3);
            TestBubbleWithHistory TestBubbleWithHistory_10_S3 = persistenceSession.get(TestBubbleWithHistoryId_10_S3);
            assertThat(TestBubbleWithHistory_10_S3.getId().getSnapshotVersion()).isEqualTo(S3);

            TestBubbleWithHistory TestBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_S3);
            assertThat(TestBubbleWithHistory_11_OLD.getId().getSnapshotVersion()).isEqualTo(S3);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForOLDAndHistoricOneByOne() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            TestBubbleWithHistory TestBubbleWithHistory_10_OLD = persistenceSession.get(TestBubbleWithHistoryId_10_OLD);
            assertThat(TestBubbleWithHistory_10_OLD.getId().getSnapshotVersion()).isEqualTo(OLD);

            // M� endre snapshot version fra OLD til S3 siden OLD er default for valgt persistence session
            persistenceSession.setSnapshot(S3);
            TestBubbleWithHistory TestBubbleWithHistory_10_S3 = persistenceSession.get(TestBubbleWithHistoryId_10_S3);
            assertThat(TestBubbleWithHistory_10_S3.getId().getSnapshotVersion()).isEqualTo(S3);

            // M� endre snapshot version tilbake fra S3 til OLD
            persistenceSession.setSnapshot(OLD);
            TestBubbleWithHistory TestBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_OLD);
            assertThat(TestBubbleWithHistory_11_OLD.getId().getSnapshotVersion()).isEqualTo(OLD);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testLoadObjectsForOLDAndHistoricTogether_Fail() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            List<BubbleId<TestBubbleWithHistory>> TestBubbleWithHistoryIds = new ArrayList<BubbleId<TestBubbleWithHistory>>();
            TestBubbleWithHistoryIds.add(TestBubbleWithHistoryId_10_OLD);
            TestBubbleWithHistoryIds.add(TestBubbleWithHistoryId_10_S3);
            TestBubbleWithHistoryIds.add(TestBubbleWithHistoryId_11_OLD);
            Collection<? extends TestBubbleWithHistory> TestBubbleWithHistorys = persistenceSession.get(TestBubbleWithHistoryIds);
        } finally {
            persistenceSession.close();
        }
    }

    public void testInsertAndCommit() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

        try {
            persistenceSession.beginTransaction();
            try {
                Session hibernateSession = persistenceSession.reserveSession();
                hibernateSession.createQuery("delete from TestBubble where id>100").executeUpdate();
            } finally {
                persistenceSession.releaseSession();
            }
            TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
            persistenceSession.insert(testBubble1);

            persistenceSession.flush();
            persistenceSession.commit();
            persistenceSession.evict(testBubble1.getId());

            TestBubble testBubble2 = persistenceSession.get(testBubble1.getId());
            assertThat(testBubble1).isNotSameAs(testBubble2).isEqualTo(testBubble2);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 200)
    public void testCommit_many() {
        testInsertAndCommit();
    }

/*
    public void testLoadHistoricObjects() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithHistory();
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerBundle(sessionFactoryBuilder, hibernateProperties);
        SnapshotManagedHibernateSession sessionManager = new SnapshotManagedHibernateSession(sessionFactoryManager);

        Session hibernateSession = null;
        try {
            TestBubbleWithHistoryId[] TestBubbleWithHistoryIds = {
                    new TestBubbleWithHistoryId(100L, SnapshotVersion.CURRENT),
                    new TestBubbleWithHistoryId(100L, SnapshotVersion.createInstance("2011-10-02 08:01:00.00")),
                    new TestBubbleWithHistoryId(100L, SnapshotVersion.createInstance("2011-10-02 08:02:00.00")),
            };
            String[] nr = {"KARTVEIEN", "KARTVEGEN", "KARTVEIEN"};

            for (int i = 0; i < TestBubbleWithHistoryIds.length; i++) {
                TestBubbleWithHistoryId TestBubbleWithHistoryId = TestBubbleWithHistoryIds[i];
                hibernateSession = sessionManager.acquireForSnapshot(TestBubbleWithHistoryId.getSnapshotVersion());
                TestBubbleWithHistory TestBubbleWithHistory = (TestBubbleWithHistory) hibernateSession.load(TestBubbleWithHistory.class, TestBubbleWithHistoryId);
                hibernateSession = sessionManager.releaseForSnapshot(hibernateSession);
                assertThat(TestBubbleWithHistory.getNavn()).isEqualTo(nr[i]);
            }
        } finally {
            sessionManager.releaseForSnapshot(hibernateSession);
        }
        sessionManager.close();
        sessionFactoryManager.close();
    }

    @Test(invocationCount = 0)
    public void testLoadHistoricObjects_many() {
        testLoadHistoricObjects();
    }

    */

}
