package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store3.persistence.PersistenceSession;
import no.statkart.skif.storetest.domain.demo.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;

import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactorManagerWithMultipleSessionsWithHistory;
import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactoryBuilderWithHistory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

/**
 * @author Henrik Fredholm
 */
@Test
public class SnapshotManagedHibernatePersistenceSessionTest {
    Properties hibernateProperties;
    static String T1 = "2011-10-02 08:01:00.00";
    static String T2 = "2011-10-02 08:02:00.00";
    static String T3 = "2011-10-02 08:03:00.00";
    static String T4 = "2011-10-02 08:04:00.00";

    HibernateSessionFactoryManager sessionFactoryManager;

    @BeforeClass
    protected void setupFactory() {
        sessionFactoryManager = createHibernateSessionFactorManagerWithMultipleSessionsWithHistory(createHibernateSessionFactoryBuilderWithHistory(), hibernateProperties);
    }

    @AfterClass
    protected void tearDownFactory() {
        sessionFactoryManager.close();
    }

    public SnapshotManagedHibernatePersistenceSessionTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }


    public void testLoadObjectWithHistory() {
        SnapshotVersion S1 = SnapshotVersion.createInstance(T1);
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));


        PersistenceSession session = null;
        try {
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            Foo foo_100_CURRENT = (Foo) session.get(new FooId(100L, SnapshotVersion.CURRENT));
            assertEquals(foo_100_CURRENT.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);

            session = sessionManager.releaseForSnapshot(session);
            session = sessionManager.acquireForSnapshot(SnapshotVersion.OLD);
            Foo foo_100_OLD = (Foo) session.get(new FooId(100L, SnapshotVersion.OLD));
            assertEquals(foo_100_OLD.getId().getSnapshotVersion(), SnapshotVersion.OLD);

            session = sessionManager.releaseForSnapshot(session);
            session = sessionManager.acquireForSnapshot(S1);
            Foo foo_100_S1 = (Foo) session.get(new FooId(100L, S1));
            assertEquals(foo_100_S1.getId().getSnapshotVersion(), S1);
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }

    }

    @Test(invocationCount = 0)
    public void testLoadObjectWithHistory_many() {
        testLoadObjectWithHistory();
    }

    public void testInsert() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            session.getWrappedSession().createQuery("delete from TestBubble where id>100").executeUpdate();
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Text 101");
            session.insert(testBubble);
            session.getWrappedSession().flush();
            sessionManager.getWrappedSessionManager().commit();

            session.getWrappedSession().clear();

            TestBubble loadedTestBuble = session.get(testBubble.getId());
            assertEquals(loadedTestBuble.getId(), testBubble.getId());
            assertEquals(loadedTestBuble.getText(), testBubble.getText());
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }

    }

    public void testUpdate() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = session.get(new TestBubbleId<TestBubble>(101));
            testBubble.setText("updated");
            session.update(testBubble);
            sessionManager.getWrappedSessionManager().commit();

            sessionManager.getWrappedSessionManager().clear();
            TestBubble loadedTestBuble = session.get(testBubble.getId());
            assertEquals(loadedTestBuble.getText(), "updated");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }

    }

    public void testUpdateDetatchNotLoaded() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            testBubble = session.update(testBubble);
            sessionManager.getWrappedSessionManager().commit();

            sessionManager.getWrappedSessionManager().clear();
            TestBubble loadedTestBuble = session.get(testBubble.getId());
            assertEquals(loadedTestBuble.getText(), "Updated");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    @Test(invocationCount = 0)
    public void testUpdateDetatchNotLoaded_many() {
        testUpdateDetatchNotLoaded();
    }

    public void testUpdateDetatchAlreadyLoaded() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            session.get(testBubble.getId());
            testBubble = session.update(testBubble);
            sessionManager.getWrappedSessionManager().commit();

            sessionManager.getWrappedSessionManager().clear();
            TestBubble loadedTestBuble = session.get(testBubble.getId());
            assertEquals(loadedTestBuble.getText(), "Updated");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    public void testDeleteNotAlreadyLoaded() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            TestBubble deletedBubble = session.delete(testBubble);
            sessionManager.getWrappedSessionManager().commit();
            assertEquals(deletedBubble.getText(), "Text 101");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    @Test(invocationCount = 0)
    public void testDeleteNotAlreadyLoaded_many() {
        testDeleteAlreadyLoaded();
    }

    /**
     * Test at objekt som slettes er det som er i databasen og ikke detatched
     */
    public void testDeleteAlreadyLoaded() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            session.get(testBubble.getId());
            TestBubble deletedBubble = session.delete(testBubble);
            sessionManager.getWrappedSessionManager().commit();
            assertEquals(deletedBubble.getText(), "Text 101");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    /**
     * Test at objekt som slettes er det som er i databasen og ikke detatched
     */
    public void testDeleteNotLoaded() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = new TestBubble(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            TestBubble deletedBubble = session.delete(testBubble);
            sessionManager.getWrappedSessionManager().commit();
            assertEquals(deletedBubble.getText(), "Text 101");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    /**
     * Test at objekt som slettes er det som har blitt oppdatet og ikke det som opprindelig var i databasen
     */
    public void testDeleteUpdated() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        testInsert();
        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestBubble testBubble = session.get(new TestBubbleId<TestBubble>(101));
            testBubble.setText("Updated");
            TestBubble deletedBubble = session.delete(testBubble);
            sessionManager.getWrappedSessionManager().commit();
            assertEquals(deletedBubble.getText(), "Updated");
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    public void testCommit() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);

            session.getWrappedSession().createQuery("delete from TestEntity where id>100").executeUpdate();
            TestEntity entity1 = new TestEntity(101L, "Entity101");
            session.getWrappedSession().save(entity1);

            session.getWrappedSession().flush();
            session = sessionManager.releaseForSnapshot(session);

            sessionManager.getWrappedSessionManager().commit();
            sessionManager.getWrappedSessionManager().close();

            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestEntity entity2 = (TestEntity) session.getWrappedSession().load(TestEntity.class, 101L);
            assertNotSame(entity1, entity2);
            assertEquals(entity1, entity2);
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    @Test(invocationCount = 0)
    public void testCommit_many() {
        testCommit();
    }


    public void testLoadHistoricObjects() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        PersistenceSession session = null;
        try {
            FooId[] fooIds = {
                    new FooId(100L, SnapshotVersion.CURRENT),
                    new FooId(100L, SnapshotVersion.createInstance("2011-10-02 08:01:00.00")),
                    new FooId(100L, SnapshotVersion.createInstance("2011-10-02 08:02:00.00")),
            };
            String[] nr = {"KARTVEIEN", "KARTVEGEN", "KARTVEIEN"};

            for (int i = 0; i < fooIds.length; i++) {
                FooId fooId = fooIds[i];
                session = sessionManager.acquireForSnapshot(fooId.getSnapshotVersion());
                Foo foo = (Foo) session.get(fooId);
                session = sessionManager.releaseForSnapshot(session);
                assertEquals(foo.getNavn(), nr[i]);
            }
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    @Test(invocationCount = 0)
    public void testLoadHistoricObjects_many() {
        testLoadHistoricObjects();
    }
}
