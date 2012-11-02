package no.statkart.skif.store.persistence;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.hibernate.engine.HibernateIterator;
import org.hibernate.jdbc.ConnectionWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;

/**
 * Tester for {@link ResourceManager} som styrer PersistenceSessions og Connections som er hentes ut via hibernate.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class ResourceManagerUsingHibernateTest {
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
                new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(0)),
                new HibernatePersistenceSessionMasterImpl(sessionFactoryManagerBundle.getBundle().get(1))
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
