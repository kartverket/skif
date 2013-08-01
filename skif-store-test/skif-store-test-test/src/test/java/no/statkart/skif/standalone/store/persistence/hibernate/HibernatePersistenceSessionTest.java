package no.statkart.skif.standalone.store.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.hibernate.DefaultHibernatePersistenceSessionImplExt;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
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

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

/**
 * Tester for {@link no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl}
 * <p>
 * Dette er en stand-alone-test som går direkte mot databasen uten å bruke StoreTestServer modulen. Mest naturlig at testene
 * kjøres i singleVM mode.
 *
 * @author Henrik Fredholm
 */
@Test(groups = "singlevm-required")
public class HibernatePersistenceSessionTest {
    Properties hibernateProperties;

    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_CURRENT = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, CURRENT);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_OLD = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, OLD);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_S3 = new TestBubbleWithHistoryId<TestBubbleWithHistory>(10L, S3);

    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_CURRENT = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, CURRENT);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_S3 = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, S3);
    TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_OLD = new TestBubbleWithHistoryId<TestBubbleWithHistory>(11L, OLD);

    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public HibernatePersistenceSessionTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
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

    public void testLoadObjectsForCurrent() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));
        try {
            TestBubbleWithHistory testBubbleWithHistory_10_CURRENT = persistenceSession.get(TestBubbleWithHistoryId_10_CURRENT);
            assertEquals(testBubbleWithHistory_10_CURRENT.getId().getSnapshotVersion(), StandAloneTestHelper.CURRENT);

            TestBubbleWithHistory testBubbleWithHistory_11_CURRENT = persistenceSession.get(TestBubbleWithHistoryId_11_CURRENT);
            assertEquals(testBubbleWithHistory_11_CURRENT.getId().getSnapshotVersion(), StandAloneTestHelper.CURRENT);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 1)
    public void testLoadObjectWithHistory_many() {
        testLoadObjectsForCurrent();
    }

    public void testLoadObjectsForOLD() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            TestBubbleWithHistory testBubbleWithHistory_10_OLD = persistenceSession.get(TestBubbleWithHistoryId_10_OLD);
            assertEquals(testBubbleWithHistory_10_OLD.getId().getSnapshotVersion(), StandAloneTestHelper.OLD);

            TestBubbleWithHistory testBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_OLD);
            assertEquals(testBubbleWithHistory_11_OLD.getId().getSnapshotVersion(), StandAloneTestHelper.OLD);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForHistoric() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            // Må endre snapshot version fra OLD til S3 siden OLD er default for valgt persistence session
            persistenceSession.setSnapshot(S3);
            TestBubbleWithHistory testBubbleWithHistory_10_S3 = persistenceSession.get(TestBubbleWithHistoryId_10_S3);
            assertEquals(testBubbleWithHistory_10_S3.getId().getSnapshotVersion(), S3);

            TestBubbleWithHistory testBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_S3);
            assertEquals(testBubbleWithHistory_11_OLD.getId().getSnapshotVersion(), S3);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForOLDAndHistoricOneByOne() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            TestBubbleWithHistory testBubbleWithHistory_10_OLD = persistenceSession.get(TestBubbleWithHistoryId_10_OLD);
            assertEquals(testBubbleWithHistory_10_OLD.getId().getSnapshotVersion(), StandAloneTestHelper.OLD);

            // Må endre snapshot version fra OLD til S3 siden OLD er default for valgt persistence session
            persistenceSession.setSnapshot(S3);
            TestBubbleWithHistory testBubbleWithHistory_10_S3 = persistenceSession.get(TestBubbleWithHistoryId_10_S3);
            assertEquals(testBubbleWithHistory_10_S3.getId().getSnapshotVersion(), S3);

            // Må endre snapshot version tilbake fra S3 til OLD
            persistenceSession.setSnapshot(StandAloneTestHelper.OLD);
            TestBubbleWithHistory testBubbleWithHistory_11_OLD = persistenceSession.get(TestBubbleWithHistoryId_11_OLD);
            assertEquals(testBubbleWithHistory_11_OLD.getId().getSnapshotVersion(), StandAloneTestHelper.OLD);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testLoadObjectsForOLDAndHistoricTogether_Fail() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(1));
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));

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
            assertNotSame(testBubble1, testBubble2);
            assertEquals(testBubble1, testBubble2);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 1)
    public void testCommit_many() {
        testInsertAndCommit();
    }

    public void testUpdate() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = persistenceSession.get(new TestBubbleId<TestBubble>(101));
            testBubble.setText("updated");
            persistenceSession.update(testBubble);
            persistenceSession.commit();

            persistenceSession.clear();
            TestBubble loadedTestBuble = persistenceSession.get(testBubble.getId());
            assertEquals(loadedTestBuble.getText(), "updated");
        } finally {
            persistenceSession.close();
        }
    }

    public void testUpdateDetatchNotLoaded() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            persistenceSession.update(testBubble);
            persistenceSession.commit();

            persistenceSession.clear();
            TestBubble loadedTestBuble = persistenceSession.get(testBubble.getId());
            assertEquals(loadedTestBuble.getText(), "Updated");
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 1)
    public void testUpdateDetatchNotLoaded_many() {
        testUpdateDetatchNotLoaded();
    }

    public void testUpdateDetatchAlreadyLoaded() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            persistenceSession.get(testBubble.getId());
            persistenceSession.update(testBubble);
            persistenceSession.commit();

            persistenceSession.clear();
            TestBubble loadedTestBuble = persistenceSession.get(testBubble.getId());
            assertEquals(loadedTestBuble.getText(), "Updated");
        } finally {
            persistenceSession.close();
        }
    }

    public void testDeleteNotAlreadyLoaded() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            persistenceSession.delete(testBubble);
            persistenceSession.commit();
            assertEquals(testBubble.getText(), "Updated");
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 1)
    public void testDeleteNotAlreadyLoaded_many() {
        testDeleteAlreadyLoaded();
    }

    /**
     * Test at objekt som slettes er det som er i databasen og ikke detatched
     */
    public void testDeleteAlreadyLoaded() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            persistenceSession.get(testBubble.getId());
            persistenceSession.delete(testBubble);
            persistenceSession.commit();
            assertEquals(testBubble.getText(), "Updated");
        } finally {
            persistenceSession.close();
        }
    }
}
