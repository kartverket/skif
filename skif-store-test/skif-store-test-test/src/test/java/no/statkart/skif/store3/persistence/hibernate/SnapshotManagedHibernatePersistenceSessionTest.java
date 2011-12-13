package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store3.persistence.PersistenceSession;
import no.statkart.skif.store3.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.domain.demo.TestEntity;
import org.hibernate.Session;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import weblogic.j2ee.descriptor.wl.PersistenceBeanDConfig;

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

    @Test(invocationCount = 200)
    public void testLoadObjectWithHistory_many() {
        testLoadObjectWithHistory();
    }

    public void testCommit() {
        SnapshotManagedHibernatePersistenceSession sessionManager = new SnapshotManagedHibernatePersistenceSession(new SnapshotManagedHibernateSession(sessionFactoryManager));

        HibernatePersistenceSession session = null;
        try {
            sessionManager.getWrappedSessionManager().beginTransaction();
            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);

            session.getWrappedSession().createQuery("delete from TestEntity").executeUpdate();
            TestEntity entity1 = new TestEntity(1L, "Entity1");
            session.getWrappedSession().save(entity1);

            session.getWrappedSession().flush();
            session = sessionManager.releaseForSnapshot(session);

            sessionManager.getWrappedSessionManager().commit();
            sessionManager.getWrappedSessionManager().close();

            session = sessionManager.acquireForSnapshot(SnapshotVersion.CURRENT);
            TestEntity entity2 = (TestEntity) session.getWrappedSession().load(TestEntity.class, 1L);
            assertNotSame(entity1, entity2);
            assertEquals(entity1, entity2);
        } finally {
            sessionManager.releaseForSnapshot(session);
            sessionManager.getWrappedSessionManager().close();
        }
    }

    @Test(invocationCount = 200)
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

    @Test(invocationCount = 200)
    public void testLoadHistoricObjects_many() {
        testLoadHistoricObjects();
    }
}
