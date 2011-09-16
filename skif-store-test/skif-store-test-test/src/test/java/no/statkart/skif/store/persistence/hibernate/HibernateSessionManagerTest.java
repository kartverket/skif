package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.persistence.ConnectionFactoryManagerSingleVersionImpl;
import no.statkart.skif.persistence.JDBCConnectionFactory;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.TestEntity;
import org.hibernate.Session;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateSessionManagerTest {
    static Object NOT_USED = new Object();

    private HibernateSessionFactoryManager hibernateSessionFactoryManager;
    private ConnectionFactoryManager connectionFactoryManager;

    public HibernateSessionManagerTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);

        org.hibernate.cfg.Configuration hibernateConfiguration = new org.hibernate.cfg.Configuration();
        hibernateConfiguration = hibernateConfiguration
                .setProperties(properties)
                .addResource("no/statkart/skif/storetest/persistence/hibernate/TestEntity.hbm.xml");

        hibernateSessionFactoryManager = new HibernateSessionFactoryManagerSingleVersionImpl(hibernateConfiguration.buildSessionFactory());

         connectionFactoryManager = new ConnectionFactoryManagerSingleVersionImpl(TestHelper.createJDBCConnectionFactory());

        assertNotNull(hibernateSessionFactoryManager);
        assertNotNull(connectionFactoryManager);
    }

    @AfterClass
    protected void close() {
        hibernateSessionFactoryManager.close();
    }

    public void testGetHibernateSessionGetConnection() throws SQLException {
        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());

        Session hibernateSession1 = sessionManager.getHibernateSession(NOT_USED);
        Session hibernateSession2 = sessionManager.getHibernateSession(NOT_USED);
        assertSame(hibernateSession1, hibernateSession2);
        assertEquals(hibernateSession1.connection().getAutoCommit(), false);
        sessionManager.close(NOT_USED);
        Session hibernateSession3 = sessionManager.getHibernateSession(NOT_USED);
        assertNotSame(hibernateSession1, hibernateSession3);
        sessionManager.close();

    }

    public void testMixSeparateHibernateSessionAndConnection_Ok() throws SQLException {
        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
        sessionManager.getHibernateSession(NOT_USED);
        sessionManager.close();
        sessionManager.getConnection(NOT_USED);
        sessionManager.close();
    }

    public void testMixSharedHibernateSessionAndConnection_Ok() throws SQLException {
        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
        sessionManager.beingAllocateConnectionsViaHibernateSession();
        Session hibernateSession1 = sessionManager.getHibernateSession(NOT_USED);
        Connection connection1 = sessionManager.getConnection(NOT_USED);
        Session hibernateSession2 = sessionManager.getHibernateSession(NOT_USED);
        Connection connection2 = sessionManager.getConnection(NOT_USED);
        assertSame(hibernateSession1, hibernateSession2);
        assertSame(connection1, connection2);
        assertSame(hibernateSession1.connection(), connection1);
        sessionManager.close();

        sessionManager.beingAllocateConnectionsViaHibernateSession();
        Connection connection3 = sessionManager.getConnection(NOT_USED);
        Session hibernateSession3 = sessionManager.getHibernateSession(NOT_USED);
        Connection connection4 = sessionManager.getConnection(NOT_USED);
        Session hibernateSession4 = sessionManager.getHibernateSession(NOT_USED);
        assertSame(hibernateSession3, hibernateSession4);
        assertSame(connection3, connection4);
        assertSame(hibernateSession3.connection(), connection3);
        sessionManager.close();

    }

    public void testMixSharedHibernateSessionAndConnection_NotOk() throws SQLException {
        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
        sessionManager.getHibernateSession(NOT_USED);
        try {
            sessionManager.getConnection(NOT_USED);
            fail();
        } catch (ImplementationException e) {
            assertEquals(e.getMessage(), "Cannot allocate independent jdbc connection since hibernate session has already been allocated");
        }
        sessionManager.close();

        sessionManager.getConnection(NOT_USED);
        try {
            sessionManager.getHibernateSession(NOT_USED);
            fail();
        } catch (ImplementationException e) {
            assertEquals(e.getMessage(), "Cannot allocate hibernate session since independent jdbc connection has already been allocated");
        }
        sessionManager.close();
    }

    public void testIsActive() throws SQLException {
        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());

        assertFalse(sessionManager.isActive(NOT_USED));
        Session hibernateSession1 = sessionManager.getHibernateSession(NOT_USED);
        assertTrue(sessionManager.isActive(NOT_USED));
        sessionManager.close();
        assertFalse(sessionManager.isActive(NOT_USED));
        Connection connection = sessionManager.getConnection(NOT_USED);
        assertTrue(sessionManager.isActive(NOT_USED));
        sessionManager.close();
        assertFalse(sessionManager.isActive(NOT_USED));
    }

    public void testHibernateSessionCommit() throws SQLException {
        HibernateSessionManager sessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, new ServiceRequestContext());
        Session hibernateSession = sessionManager.getHibernateSession(NOT_USED);
        sessionManager.beginTransaction();
        hibernateSession.createQuery("delete from TestEntity").executeUpdate();
        TestEntity entity1 = new TestEntity(1L, "Entity1");
        hibernateSession.save(entity1);
        sessionManager.commit();
        sessionManager.close();

        TestEntity entity2 = (TestEntity) sessionManager.getHibernateSession(NOT_USED).load(TestEntity.class, 1L);

        assertEquals(entity1, entity2);
        sessionManager.close();
    }
}
