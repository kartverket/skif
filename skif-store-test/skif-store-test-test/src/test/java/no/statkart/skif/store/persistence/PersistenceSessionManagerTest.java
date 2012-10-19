package no.statkart.skif.store.persistence;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
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
import static org.fest.assertions.api.Assertions.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 */
@Test
public class PersistenceSessionManagerTest {
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
    FooId<Foo> FooId_100_S4 = new FooId<Foo>(100L, S4);

    FooId<Foo> FooId_101_CURRENT = new FooId<Foo>(101L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_101_S3 = new FooId<Foo>(101L, S3);
    FooId<Foo> FooId_101_S4 = new FooId<Foo>(101L, S4);
    FooId<Foo> FooId_101_OLD = new FooId<Foo>(101L, SnapshotVersion.OLD);

    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public PersistenceSessionManagerTest() {
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
            Foo foo_100_CURRENT = persistenceSession.get(FooId_100_CURRENT);
            assertEquals(foo_100_CURRENT.getId().getSnapshotVersion(), CURRENT);

            Foo foo_100_OLD = persistenceSession.get(FooId_100_OLD);
            assertEquals(foo_100_OLD.getId().getSnapshotVersion(), OLD);


            Foo foo_100_S3 = persistenceSession.get(FooId_100_S3);
            assertEquals(foo_100_S3.getId().getSnapshotVersion(), S3);

            try {
                Session session = persistenceSession.getForSnapshotVersion(OLD).getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
                Foo foo_101_OLD = persistenceSession.get(FooId_101_OLD);
                assertEquals(foo_101_OLD.getId().getSnapshotVersion(), OLD);
            } finally {
                persistenceSession.getForSnapshotVersion(OLD).getImplementation(HibernatePersistenceSessionMaster.class).releaseSession();
            }
            Foo foo_101_S3 = persistenceSession.get(FooId_101_S3);
            assertEquals(foo_101_S3.getId().getSnapshotVersion(), S3);

            Foo foo_101_CURRENT = persistenceSession.get(FooId_101_CURRENT);
            assertEquals(foo_101_CURRENT.getId().getSnapshotVersion(), CURRENT);
        } finally {
            persistenceSession.close();
        }
    }

    public void testLoadObjectsForMCollectionWithultipleSnapshotVersions() {
        PersistenceSessionManager persistenceSession = createPersistenceSessionManager();

        try {
            List<FooId<Foo>> ids = new ArrayList<FooId<Foo>>();
            ids.add(FooId_100_CURRENT);
            ids.add(FooId_100_OLD);
            ids.add(FooId_101_OLD);
            ids.add(FooId_101_CURRENT);
            ids.add(FooId_100_S3);
            ids.add(FooId_100_S4);
            ids.add(FooId_101_S4);
            Collection<? extends Foo> foos = persistenceSession.get(ids);
            assertThat(extractProperty("id").from(foos)).containsOnly(FooId_100_CURRENT, FooId_100_OLD, FooId_101_OLD, FooId_101_CURRENT, FooId_100_S3, FooId_100_S4, FooId_101_S4);
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
            FooId[] fooIds = {
                    new FooId(100L, SnapshotVersion.CURRENT),
                    new FooId(100L, SnapshotVersion.createInstance("2011-10-02 08:01:00.00")),
                    new FooId(100L, SnapshotVersion.createInstance("2011-10-02 08:02:00.00")),
            };
            String[] nr = {"KARTVEIEN", "KARTVEGEN", "KARTVEIEN"};

            for (int i = 0; i < fooIds.length; i++) {
                FooId fooId = fooIds[i];
                hibernateSession = sessionManager.acquireForSnapshot(fooId.getSnapshotVersion());
                Foo foo = (Foo) hibernateSession.load(Foo.class, fooId);
                hibernateSession = sessionManager.releaseForSnapshot(hibernateSession);
                assertEquals(foo.getNavn(), nr[i]);
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
