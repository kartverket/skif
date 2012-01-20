package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.hibernate.Session;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

/**
 * Tester for {@link HibernatePersistenceSessionMasterImpl}
 * @author Henrik Fredholm
 */
@Test
public class DefaultHibernatePersistenceSessionTest {
    Properties hibernateProperties;

    static String T1 = "2011-10-02 08:01:00.00";
    static String T2 = "2011-10-02 08:02:00.00";
    static String T3 = "2011-10-02 08:03:00.00";
    static String T4 = "2011-10-02 08:04:00.00";
    static SnapshotVersion CURRENT = SnapshotVersion.CURRENT;
    static SnapshotVersion OLD = SnapshotVersion.OLD;
    static SnapshotVersion S1 = SnapshotVersion.createInstance(T1);
    static SnapshotVersion S2 = SnapshotVersion.createInstance(T2);
    static SnapshotVersion S3 = SnapshotVersion.createInstance(T3);
    static SnapshotVersion S4 = SnapshotVersion.createInstance(T4);

    FooId<Foo> FooId_100_CURRENT = new FooId<Foo>(100L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_100_OLD = new FooId<Foo>(100L, SnapshotVersion.OLD);
    FooId<Foo> FooId_100_S1 = new FooId<Foo>(100L, S1);
    FooId<Foo> FooId_100_S2 = new FooId<Foo>(100L, S2);
    FooId<Foo> FooId_100_S3 = new FooId<Foo>(100L, S3);

    FooId<Foo> FooId_101_CURRENT = new FooId<Foo>(101L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_101_S3 = new FooId<Foo>(101L, S3);
    FooId<Foo> FooId_101_S4 = new FooId<Foo>(101L, S4);
    FooId<Foo> FooId_101_OLD = new FooId<Foo>(101L, SnapshotVersion.OLD);

    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public DefaultHibernatePersistenceSessionTest() {
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));
        try {
            Foo foo_100_CURRENT = persistenceSession.get(FooId_100_CURRENT);
            assertEquals(foo_100_CURRENT.getId().getSnapshotVersion(), CURRENT);

            Foo foo_101_CURRENT = persistenceSession.get(FooId_101_CURRENT);
            assertEquals(foo_101_CURRENT.getId().getSnapshotVersion(), CURRENT);
        } finally {
            persistenceSession.close();
        }
    }


    @Test(invocationCount = 1)
    public void testLoadObjectWithHistory_many() {
        testLoadObjectsForCurrent();
    }


    public void testLoadObjectsForOLD() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            Foo foo_100_OLD = persistenceSession.get(FooId_100_OLD);
            assertEquals(foo_100_OLD.getId().getSnapshotVersion(), OLD);

            Foo foo_101_OLD = persistenceSession.get(FooId_101_OLD);
            assertEquals(foo_101_OLD.getId().getSnapshotVersion(), OLD);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForHistoric() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            // Må endre snapshot version fra OLD til S3 siden OLD er default for valgt persistence session
            persistenceSession.setSnapshot(S3);
            Foo foo_100_S3 = persistenceSession.get(FooId_100_S3);
            assertEquals(foo_100_S3.getId().getSnapshotVersion(), S3);

            Foo foo_101_OLD = persistenceSession.get(FooId_101_S3);
            assertEquals(foo_101_OLD.getId().getSnapshotVersion(), S3);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForOLDAndHistoricOneByOne() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            Foo foo_100_OLD = persistenceSession.get(FooId_100_OLD);
            assertEquals(foo_100_OLD.getId().getSnapshotVersion(), OLD);

            // Må endre snapshot version fra OLD til S3 siden OLD er default for valgt persistence session
            persistenceSession.setSnapshot(S3);
            Foo foo_100_S3 = persistenceSession.get(FooId_100_S3);
            assertEquals(foo_100_S3.getId().getSnapshotVersion(), S3);

            // Må endre snapshot version tilbake fra S3 til OLD
            persistenceSession.setSnapshot(OLD);
            Foo foo_101_OLD = persistenceSession.get(FooId_101_OLD);
            assertEquals(foo_101_OLD.getId().getSnapshotVersion(), OLD);
        } finally {
            persistenceSession.close();
        }
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testLoadObjectsForOLDAndHistoricTogether_Fail() {
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1));
        try {
            List<BubbleId<Foo>> fooIds = new ArrayList<BubbleId<Foo>>();
            fooIds.add(FooId_100_OLD);
            fooIds.add(FooId_100_S3);
            fooIds.add(FooId_101_OLD);
            Collection<? extends Foo> foos = persistenceSession.get(fooIds);
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
            TestBubble testBubble1 = new TestBubble(new TestBubbleId<TestBubble>(101L), "TestBubble 101");
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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

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
        HibernatePersistenceSessionMasterImpl persistenceSession = new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0));

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
