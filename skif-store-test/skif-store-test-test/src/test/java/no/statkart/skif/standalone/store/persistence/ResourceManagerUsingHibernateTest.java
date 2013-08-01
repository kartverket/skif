package no.statkart.skif.standalone.store.persistence;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import org.hibernate.jdbc.ConnectionWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.*;
import static org.testng.Assert.assertSame;

/**
 * Tester for {@link ResourceManager} som styrer PersistenceSessions og Connections som er hentes ut via hibernate.
 * <p>
 * Dette er en stand-alone-test som går direkte mot databasen uten å bruke StoreTestServer modulen. Mest naturlig at testene
 * kjøres i singleVM mode.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class ResourceManagerUsingHibernateTest {

    Properties hibernateProperties;
    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;
    DefaultPersistenceSessionManager persistenceSessionManager;
    ConnectionManagerUsingHibernate connectionManager;
    ResourceManager resourceManager;

    public ResourceManagerUsingHibernateTest() {
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

        resourceManager = new DefaultResourceManager(
                new ResourceManager.Entry(persistenceSessionManager,PersistenceSessionManager.class), new ResourceManager.Entry(connectionManager, ConnectionManager.class)
                );
        resourceManager.start();

    }

    @AfterClass
    void tearDown() {
        resourceManager.close();
        resourceManager.shutdown();
    }

    /**
     * Tester oppslag på resource via implementasjonsklasse og interface
     * @throws SQLException
     */
    public void testGetResource() throws SQLException {
        ConnectionManager connectionManager = resourceManager.getResource(ConnectionManagerUsingHibernate.class);
        assertSame(connectionManager.getClass(), ConnectionManagerUsingHibernate.class);
        assertSame(connectionManager,resourceManager.getResource(ConnectionManagerUsingHibernate.class) );
        assertSame(connectionManager,resourceManager.getResource(ConnectionManager.class) );
        Connection connectionViaConnectionManager = resourceManager.getResource(ConnectionManager.class).getForSnapshotVersion(SnapshotVersion.CURRENT).reserve();

        PersistenceSessionManager persistenceSessionManager = resourceManager.getResource(DefaultPersistenceSessionManager.class);
        assertSame(persistenceSessionManager.getClass(), DefaultPersistenceSessionManager.class);
        assertSame(persistenceSessionManager, resourceManager.getResource(DefaultPersistenceSessionManager.class));
        assertSame(persistenceSessionManager,resourceManager.getResource(PersistenceSessionManager.class) );
        Connection connectionFromSession = resourceManager.getResource(PersistenceSessionManager.class).getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMaster.class).reserveSession().connection();

        // Hibernate putter på en wrapper når man henter ut en connection. Men det er samme underliggende connection
        assertSame(ConnectionWrapper.class.cast(connectionFromSession).getWrappedConnection(), ConnectionWrapper.class.cast(connectionViaConnectionManager).getWrappedConnection());
   }

}
