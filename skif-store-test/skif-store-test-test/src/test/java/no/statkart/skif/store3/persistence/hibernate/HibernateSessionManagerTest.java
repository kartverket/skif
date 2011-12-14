package no.statkart.skif.store3.persistence.hibernate;

import com.google.inject.Provider;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerMultiVersionImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionManagerMultiVersionImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionProvider;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Jan Holmen
 */
@Test
public class HibernateSessionManagerTest {
    private HibernateSessionFactoryManagerImpl hibernateSessionFactoryManager;


    private no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate");
        return builder;
    }


    private Properties getHibernateProperties() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return properties;
    }


    private void ikkeTest() {
        no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        HibernateSessionFactoryManagerMultiVersionImpl hibernateSessionFactoryManager = new HibernateSessionFactoryManagerMultiVersionImpl((sfbuilder));
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManager hibernateSessionManager = new HibernateSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);

        Provider<Session> sessionProvider = new HibernateSessionProvider(hibernateSessionManager, SnapshotVersion.CURRENT);
        Session s = sessionProvider.get();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);

    }


    public void testHibernateSessionManager() {

        HibernateSessionFactoryDescriptor descriptor1 = new HibernateSessionFactoryDescriptor("current", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, getHibernateProperties());
        HibernateSessionFactoryDescriptor descriptor2 = new HibernateSessionFactoryDescriptor("old", new SnapshotVersionSeed(SnapshotVersion.OLD), false, getHibernateProperties());
        hibernateSessionFactoryManager = new HibernateSessionFactoryManagerImpl(createHibernateSessionFactoryBuilder().addResource(TestBubble.class), descriptor1, descriptor2);
        assertNotNull(hibernateSessionFactoryManager);

        SessionFactory factory0 = hibernateSessionFactoryManager.getFactory(0);
        SessionFactory factory1 = hibernateSessionFactoryManager.getFactory(1);
        assertNotSame(factory0, factory1);

        org.hibernate.classic.Session session0 = factory0.openSession();
        List list = session0.createQuery("from TestBubble").list();
        assertNotNull(list);
        assertTrue(list.size() > 0);
        TestBubble tb = (TestBubble) list.get(0);
        assertEquals(tb.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
        int antall = session0.createSQLQuery("delete from testbubble").executeUpdate();
        assertTrue(antall == 2);
        session0.flush();


        List listEmpty = session0.createQuery("from TestBubble").list();
        assertNotNull(listEmpty);
        assertTrue(listEmpty.size() == 0);


        org.hibernate.classic.Session session1 = factory1.openSession();
        List list1 = session1.createQuery("from TestBubble").list();
        assertNotNull(list1);
        assertTrue(list1.size() > 0);
        TestBubble tb1 = (TestBubble) list1.get(0);
        assertEquals(tb1.getId().getSnapshotVersion(), SnapshotVersion.OLD);

        session1.flush();


        org.hibernate.classic.Session session = factory0.openSession();
        session.createSQLQuery("insert into testbubble values (1,'Text 1')").executeUpdate();
        session.createSQLQuery("insert into testbubble values (2,'Text 2')").executeUpdate();
        session.flush();

        try {
            hibernateSessionFactoryManager.close();
        } catch (Exception e) {
            fail("Klarte ikke å stenge manager");
        }

    }


    public void testAccepts() {
        HibernateSessionFactoryDescriptor descriptor1 = new HibernateSessionFactoryDescriptor("current", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true,  getHibernateProperties());
        HibernateSessionFactoryDescriptor descriptor2 = new HibernateSessionFactoryDescriptor("old", new SnapshotVersionSeed(SnapshotVersion.OLD), true, getHibernateProperties());

        assertTrue(descriptor1.accepts(SnapshotVersion.CURRENT));
        assertFalse(descriptor1.accepts(SnapshotVersion.OLD));
        assertFalse(descriptor1.accepts(SnapshotVersion.createInstance("2015-11-21 13:13:35.2")));

        assertTrue(descriptor2.accepts(SnapshotVersion.OLD));
        assertFalse(descriptor2.accepts(SnapshotVersion.CURRENT));
        assertTrue(descriptor2.accepts(SnapshotVersion.createInstance("2015-11-21 13:13:35.2")));

    }

    @AfterClass
    protected void close() {
        if (hibernateSessionFactoryManager != null) hibernateSessionFactoryManager.close();
    }

//    public void testGetHibernateSessionGetConnection() throws SQLException {
//        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
//
//        Session hibernateSession1 = sessionManager.getHibernateSession(NOT_USED);
//        Session hibernateSession2 = sessionManager.getHibernateSession(NOT_USED);
//        assertSame(hibernateSession1, hibernateSession2);
//        assertEquals(hibernateSession1.connection().getAutoCommit(), false);
//        sessionManager.close(NOT_USED);
//        Session hibernateSession3 = sessionManager.getHibernateSession(NOT_USED);
//        assertNotSame(hibernateSession1, hibernateSession3);
//        sessionManager.close();
//
//    }

//    public void testMixSeparateHibernateSessionAndConnection_Ok() throws SQLException {
//        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
//        sessionManager.getHibernateSession(NOT_USED);
//        sessionManager.close();
//        sessionManager.getConnection(NOT_USED);
//        sessionManager.close();
//    }

//    public void testMixSharedHibernateSessionAndConnection_Ok() throws SQLException {
//        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
//        sessionManager.beingAllocateConnectionsViaHibernateSession();
//        Session hibernateSession1 = sessionManager.getHibernateSession(NOT_USED);
//        Connection connection1 = sessionManager.getConnection(NOT_USED);
//        Session hibernateSession2 = sessionManager.getHibernateSession(NOT_USED);
//        Connection connection2 = sessionManager.getConnection(NOT_USED);
//        assertSame(hibernateSession1, hibernateSession2);
//        assertSame(connection1, connection2);
//        assertSame(hibernateSession1.connection(), connection1);
//        sessionManager.close();
//
//        sessionManager.beingAllocateConnectionsViaHibernateSession();
//        Connection connection3 = sessionManager.getConnection(NOT_USED);
//        Session hibernateSession3 = sessionManager.getHibernateSession(NOT_USED);
//        Connection connection4 = sessionManager.getConnection(NOT_USED);
//        Session hibernateSession4 = sessionManager.getHibernateSession(NOT_USED);
//        assertSame(hibernateSession3, hibernateSession4);
//        assertSame(connection3, connection4);
//        assertSame(hibernateSession3.connection(), connection3);
//        sessionManager.close();
//
//    }

//    public void testMixSharedHibernateSessionAndConnection_NotOk() throws SQLException {
//        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
//        sessionManager.getHibernateSession(NOT_USED);
//        try {
//            sessionManager.getConnection(NOT_USED);
//            fail();
//        } catch (ImplementationException e) {
//            assertEquals(e.getMessage(), "Cannot allocate independent jdbc connection since hibernate session has already been allocated");
//        }
//        sessionManager.close();
//
//        sessionManager.getConnection(NOT_USED);
//        try {
//            sessionManager.getHibernateSession(NOT_USED);
//            fail();
//        } catch (ImplementationException e) {
//            assertEquals(e.getMessage(), "Cannot allocate hibernate session since independent jdbc connection has already been allocated");
//        }
//        sessionManager.close();
//    }

//    public void testIsActive() throws SQLException {
//        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
//
//        assertFalse(sessionManager.isActive(NOT_USED));
//        Session hibernateSession1 = sessionManager.getHibernateSession(NOT_USED);
//        assertTrue(sessionManager.isActive(NOT_USED));
//        sessionManager.close();
//        assertFalse(sessionManager.isActive(NOT_USED));
//        Connection connection = sessionManager.getConnection(NOT_USED);
//        assertTrue(sessionManager.isActive(NOT_USED));
//        sessionManager.close();
//        assertFalse(sessionManager.isActive(NOT_USED));
//    }

//    public void testHibernateSessionCommit() throws SQLException {
//        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
//        Session hibernateSession = sessionManager.getHibernateSession(NOT_USED);
//        sessionManager.beginTransaction();
//        hibernateSession.createQuery("delete from TestEntity where id>100").executeUpdate();
//        TestEntity entity1 = new TestEntity(101L, "Entity101");
//        hibernateSession.save(entity1);
//        sessionManager.commit();
//        sessionManager.close();
//
//        TestEntity entity2 = (TestEntity) sessionManager.getHibernateSession(NOT_USED).load(TestEntity.class, 1L);
//
//        assertEquals(entity1, entity2);
//        sessionManager.close();
//    }
}
