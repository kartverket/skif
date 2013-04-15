package no.statkart.skif.store.persistence.jdbc;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.DefaultHibernatePersistenceSessionImplExt;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import org.hibernate.jdbc.ConnectionWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.testng.Assert.*;

/**
 * Tester for {@link  ConnectionManagerUsingHibernate}
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class ConnectionManagerUsingHibernateTest {
    Properties hibernateProperties;
    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;
    DefaultPersistenceSessionManager persistenceSessionManager;
    ConnectionManagerUsingHibernate connectionManager;

    public ConnectionManagerUsingHibernateTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }

    @BeforeClass
    public void setUp() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithHistory();
        sessionFactoryManagerBundle = createHibernateSessionFactorManagerBundle(sessionFactoryBuilder, hibernateProperties);
        persistenceSessionManager = new DefaultPersistenceSessionManager(
                new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(0)),
                new DefaultHibernatePersistenceSessionImplExt(sessionFactoryManagerBundle.getBundle().get(1))
        );
        connectionManager = new ConnectionManagerUsingHibernate(persistenceSessionManager);
    }

    @AfterClass
    void tearDown() {
        persistenceSessionManager.close();
        sessionFactoryManagerBundle.close();
    }

    public void testAllocateConnection() throws SQLException {

        ConnectionForSnapshotVersion connectionForSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(connectionForSnapshotVersion.getAutoCommit());

        assertSame(connectionForSnapshotVersion, connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT));

        try {
            Connection wrappedConnection = connectionForSnapshotVersion.reserve();
            Connection unwrappedConnection = ConnectionWrapper.class.cast(wrappedConnection).getWrappedConnection();
            assertEquals(unwrappedConnection.getClass().getName(), "oracle.jdbc.driver.T4CConnection");
        } finally {
            connectionForSnapshotVersion.release();
        }

        Connection connectionOld = connectionManager.getForSnapshotVersion(SnapshotVersion.OLD);
        assertFalse(connectionForSnapshotVersion.getAutoCommit());

        assertNotSame(connectionForSnapshotVersion, connectionOld);

        // Denne gjøre ingen ting
        connectionManager.close();

        // Det er denne som lukker
        persistenceSessionManager.close();
    }

    @Test(invocationCount = 200)
    public void testAllocateConnection_many() throws SQLException {
        testAllocateConnection();
    }

}
