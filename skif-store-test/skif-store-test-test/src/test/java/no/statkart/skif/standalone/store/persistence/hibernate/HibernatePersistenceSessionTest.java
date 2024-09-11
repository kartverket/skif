package no.statkart.skif.standalone.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.storetest.domain.standalone.ParentBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleId;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistory;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistoryId;
import org.hibernate.Session;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.CURRENT;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.OLD;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.S3;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactoryBuilderWithHistory;
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
    private Properties hibernateProperties;

    private TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_CURRENT = new TestBubbleWithHistoryId<>(10L, CURRENT);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_OLD = new TestBubbleWithHistoryId<>(10L, OLD);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_10_S3 = new TestBubbleWithHistoryId<>(10L, S3);

    private TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_CURRENT = new TestBubbleWithHistoryId<>(11L, CURRENT);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_S3 = new TestBubbleWithHistoryId<>(11L, S3);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> TestBubbleWithHistoryId_11_OLD = new TestBubbleWithHistoryId<>(11L, OLD);

    private final TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<>(101L);

    private HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public HibernatePersistenceSessionTest() {
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

    public void testLoadObjectsForCurrent() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));
        try {
            TestBubbleWithHistory testBubbleWithHistory_10_CURRENT = persistenceSession.get(TestBubbleWithHistoryId_10_CURRENT);
            assertEquals(testBubbleWithHistory_10_CURRENT.getId().getSnapshotVersion(), StandAloneTestHelper.CURRENT);

            TestBubbleWithHistory testBubbleWithHistory_11_CURRENT = persistenceSession.get(TestBubbleWithHistoryId_11_CURRENT);
            assertEquals(testBubbleWithHistory_11_CURRENT.getId().getSnapshotVersion(), StandAloneTestHelper.CURRENT);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 2)
    public void testLoadObjectWithHistory_many() {
        testLoadObjectsForCurrent();
    }

    public void testLoadObjectsForOLD() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            List<BubbleId<TestBubbleWithHistory>> TestBubbleWithHistoryIds = new ArrayList<>();
            TestBubbleWithHistoryIds.add(TestBubbleWithHistoryId_10_OLD);
            TestBubbleWithHistoryIds.add(TestBubbleWithHistoryId_10_S3);
            TestBubbleWithHistoryIds.add(TestBubbleWithHistoryId_11_OLD);
            persistenceSession.get(TestBubbleWithHistoryIds);
        } finally {
            persistenceSession.close();
        }
    }

    @SuppressWarnings({"JpaQlInspection", "Duplicates"})
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
            assertNotSame(testBubble1, testBubble2);
            assertEquals(testBubble1, testBubble2);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 2)
    public void testCommit_many() {
        testInsertAndCommit();
    }

    public void testUpdate() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = persistenceSession.get(new TestBubbleId<>(101L));
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<>(101L));
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

    @Test(invocationCount = 2)
    public void testUpdateDetatchNotLoaded_many() {
        testUpdateDetatchNotLoaded();
    }

    public void testUpdateDetatchAlreadyLoaded() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<>(101L));
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<>(101L));
            testBubble.setText("Updated");
            persistenceSession.delete(testBubble);
            persistenceSession.commit();
            assertEquals(testBubble.getText(), "Updated");
        } finally {
            persistenceSession.close();
        }
    }

    @Test(invocationCount = 2)
    public void testDeleteNotAlreadyLoaded_many() {
        testDeleteAlreadyLoaded();
    }

    /**
     * Test at objekt som slettes er det som er i databasen og ikke detatched
     */
    public void testDeleteAlreadyLoaded() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

        testInsertAndCommit();
        try {
            persistenceSession.beginTransaction();
            TestBubble testBubble = new TestBubble(new TestBubbleId<>(101L));
            testBubble.setText("Updated");
            persistenceSession.get(testBubble.getId());
            persistenceSession.delete(testBubble);
            persistenceSession.commit();
            assertEquals(testBubble.getText(), "Updated");
        } finally {
            persistenceSession.close();
        }
    }

    /**
     * Tester at buildCriterias oppretter Criteria i samme iterasjonsrekkefølge som objektklassene forekommer første
     * gang i {@code ids}.
     */
    public void testDeterministiskLoadOrderForBuildCriterias() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));
        try {
            LinkedHashSet<BubbleId<?>> ids1 = new LinkedHashSet<>();
            ids1.add(new ParentBubbleId<>(1L));
            ids1.add(new TestBubbleId<>(1L));
            ids1.add(new ParentBubbleId<>(2L));
            final List<CriteriaQuery<BubbleObject>> criteriaList1 = persistenceSession.buildCriterias(ids1);
            assertEquals(criteriaList1.size(), 2);
            assertEquals(criteriaList1.get(0).getResultType(), ParentBubble.class);
            assertEquals(criteriaList1.get(1).getResultType(), TestBubble.class);

            LinkedHashSet<BubbleId<?>> ids2 = new LinkedHashSet<>();
            ids2.add(new TestBubbleId<>(1L));
            ids2.add(new ParentBubbleId<>(1L));
            ids2.add(new ParentBubbleId<>(2L));
            final List<CriteriaQuery<BubbleObject>> criteriaList2 = persistenceSession.buildCriterias(ids2);
            assertEquals(criteriaList2.size(), 2);
            assertEquals(criteriaList2.get(0).getResultType(), TestBubble.class);
            assertEquals(criteriaList2.get(1).getResultType(), ParentBubble.class);
        } finally {
            persistenceSession.close();
        }
    }

}
