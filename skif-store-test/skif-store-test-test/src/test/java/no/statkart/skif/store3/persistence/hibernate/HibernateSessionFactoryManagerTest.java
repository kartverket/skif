package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store3.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.demo.*;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.testng.annotations.Test;

import java.util.Properties;

import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactorManagerWithMultipleSessionsNoHistory;
import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactorManagerWithMultipleSessionsWithHistory;
import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactorManagerWithSingleSessionNoHistory;
import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateSessionFactoryManagerTest {
    Properties hibernateProperties;

    public HibernateSessionFactoryManagerTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);


    }

    /**
     * Builder som ikke inneholder bobler med historikk
     *
     * @return
     */
    private HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilderWithNoHistory() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                .addResource(TestEntity.class)
                .addResource(TestBubble.class);
    }

    /**
     * Builder som inneholder bobler med historikk.
     *
     * @return
     */
    private HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilderWithHistory() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                .addResource(TestEntity.class)
                .addResource(TestBubble.class)
                .addResource(Foo.class);


    }

    /**
     * Tester factory manager mot et schema hvor snapshot version ikke settes og manageren kun har en session som bruker {@code SnapshotVersion.CURRENT}
     */

    public void testOpenAndCloseForSingleFactoryManager() {
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerWithSingleSessionNoHistory(createHibernateSessionFactoryBuilderWithNoHistory(), hibernateProperties);

        SessionFactory factory = sessionFactoryManager.getFactory(0);

        assertSame(sessionFactoryManager.getFactory(0), factory);
        assertSame(sessionFactoryManager.getPersistenceDescriptors()[0].getObject(), factory);
        assertTrue(sessionFactoryManager.getPersistenceDescriptors()[0].accepts(SnapshotVersion.CURRENT));
        assertFalse(sessionFactoryManager.getPersistenceDescriptors()[0].accepts(SnapshotVersion.OLD));

        sessionFactoryManager.close();
        assertSame(sessionFactoryManager.getPersistenceDescriptors()[0].getObject(), null, "Factory ble ikke satt til null ved kall til close");

        SessionFactory factory2 = sessionFactoryManager.getFactory(0);
        assertNotNull(factory2);
        assertNotSame(factory2, factory);

        sessionFactoryManager.close();

    }

    @Test(invocationCount = 0)
    public void testOpenAndCloseForSingleFactoryManager_Many() {
        testOpenAndCloseForSingleFactoryManager();
    }

    public void testMultipleCallsToClose() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithNoHistory();
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerWithSingleSessionNoHistory(sessionFactoryBuilder, hibernateProperties);

        sessionFactoryManager.close();
        sessionFactoryManager.close();
    }

    public void testCreateSessionAndLoadObject() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithNoHistory();
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerWithSingleSessionNoHistory(sessionFactoryBuilder, hibernateProperties);

        //sessionFactoryManager.startService();
        SessionFactory factory = sessionFactoryManager.getFactory(0);
        Session session = factory.openSession();

        TestEntity testEntity = (TestEntity) session.load(TestEntity.class, new Long(1));
        assertNotNull(testEntity);
        assertEquals(testEntity.getId(), new Long(1));

        TestBubbleId testBubbleId_1_CURRENT = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        TestBubble testBubble = (TestBubble) session.load(TestBubble.class, testBubbleId_1_CURRENT);
        assertNotNull(testBubble);
        assertEquals(testBubble.getId(), testBubbleId_1_CURRENT);


        session.close();
        sessionFactoryManager.close();
        //sessionFactoryManager.endService();
    }

    @Test(invocationCount = 0)
    public void testCreateSessionAndLoadObject_Many() {
        testCreateSessionAndLoadObject();
    }


    public void testOpenAndCloseForMultiFactoryManager() {
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerWithMultipleSessionsNoHistory(createHibernateSessionFactoryBuilderWithNoHistory(), hibernateProperties);

        SessionFactory factory0 = sessionFactoryManager.getFactory(0);
        SessionFactory factory1 = sessionFactoryManager.getFactory(1);

        assertSame(sessionFactoryManager.getFactory(0), factory0);
        assertSame(sessionFactoryManager.getPersistenceDescriptors()[0].getObject(), factory0);
        assertSame(sessionFactoryManager.getFactory(1), factory1);
        assertSame(sessionFactoryManager.getPersistenceDescriptors()[1].getObject(), factory1);

        sessionFactoryManager.close();
        assertSame(sessionFactoryManager.getPersistenceDescriptors()[0].getObject(), null, "Factory ble ikke satt til null ved kall til close");
        assertSame(sessionFactoryManager.getPersistenceDescriptors()[1].getObject(), null, "Factory ble ikke satt til null ved kall til close");
    }

    public void testCreateSessionAndLoadObjectInMultiSession() {
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerWithMultipleSessionsNoHistory(createHibernateSessionFactoryBuilderWithNoHistory(), hibernateProperties);

        SessionFactory factory0 = sessionFactoryManager.getFactory(0);
        SessionFactory factory1 = sessionFactoryManager.getFactory(1);

        Session session0 = factory0.openSession();
        TestEntity testEntity_CURRENT = (TestEntity) session0.load(TestEntity.class, new Long(1));
        Session session1 = factory1.openSession();
        TestEntity testEntity_OLD = (TestEntity) session1.load(TestEntity.class, new Long(1));
        assertNotSame(testEntity_CURRENT, testEntity_OLD);

        TestBubbleId testBubbleId_1_CURRENT = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        TestBubble testBubble_1_CURRENT = (TestBubble) session0.load(TestBubble.class, testBubbleId_1_CURRENT);

        TestBubbleId testBubbleId_1_OLD = new TestBubbleId(1L, SnapshotVersion.OLD);
        TestBubble testBubble_1_OLD = (TestBubble) session1.load(TestBubble.class, testBubbleId_1_OLD);

        assertNotNull(testBubble_1_CURRENT);
        assertNotNull(testBubble_1_OLD);
        assertEquals(testBubble_1_CURRENT.getId(), testBubbleId_1_CURRENT);
        assertEquals(testBubble_1_OLD.getId(), testBubbleId_1_OLD);

        sessionFactoryManager.close();
    }

    public void testCreateSessionAndLoadObjectInMultiSessionUsingFactoryWithHistorySupport() {
        HibernateSessionFactoryManager sessionFactoryManager = createHibernateSessionFactorManagerWithMultipleSessionsWithHistory(createHibernateSessionFactoryBuilderWithHistory(), hibernateProperties);

        SessionFactory factory0 = sessionFactoryManager.getFactory(0);
        SessionFactory factory1 = sessionFactoryManager.getFactory(1);

        Session session0 = factory0.openSession();
        sessionFactoryManager.getPersistenceDescriptors()[0].setSnapshotVersion(session0, SnapshotVersion.CURRENT);
        Session session1 = factory1.openSession();
        sessionFactoryManager.getPersistenceDescriptors()[1].setSnapshotVersion(session1, SnapshotVersion.OLD);

        TestEntity testEntity_CURRENT = (TestEntity) session0.load(TestEntity.class, new Long(1));
        TestEntity testEntity_OLD = (TestEntity) session1.load(TestEntity.class, new Long(1));
        assertNotSame(testEntity_CURRENT, testEntity_OLD);

        TestBubbleId testBubbleId_1_CURRENT = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        TestBubble testBubble_1_CURRENT = (TestBubble) session0.load(TestBubble.class, testBubbleId_1_CURRENT);
        FooId fooId_1_CURRENT = new FooId(100L, SnapshotVersion.CURRENT);
        Foo foo_1_CURRENT = (Foo) session0.load(Foo.class, fooId_1_CURRENT);
        assertNotNull(testBubble_1_CURRENT);
        assertNotNull(foo_1_CURRENT);
        assertEquals(foo_1_CURRENT.getId(), fooId_1_CURRENT);


        TestBubbleId testBubbleId_1_OLD = new TestBubbleId(1L, SnapshotVersion.OLD);
        TestBubble testBubble_1_OLD = (TestBubble) session1.load(TestBubble.class, testBubbleId_1_OLD);
        FooId fooId_100_OLD = new FooId(100L, SnapshotVersion.OLD);
        Foo foo_100_OLD = (Foo) session1.load(Foo.class, fooId_100_OLD);

        assertNotNull(testBubble_1_OLD);
        assertEquals(testBubble_1_OLD.getId(), testBubbleId_1_OLD);
        assertEquals(foo_100_OLD.getId(), fooId_100_OLD);

        sessionFactoryManager.close();

    }

}
