package no.statkart.skif.standalone.store.persistence.jdbc;

import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import oracle.jdbc.OracleConnection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * Tester for {@link  no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate}
 * <p>
 * Dette er en stand-alone-test som går direkte mot databasen uten å bruke StoreTestServer. Mest naturlig at testene
 * kjøres i singleVM mode.
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
        hibernateProperties = StandAloneTestHelper.createHibernatePropertiesSingleVm();
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
            OracleConnection oracleConnection = wrappedConnection.unwrap(OracleConnection.class);// Sjekk at vi har muligheten for å få tak i denne
            assertEquals(oracleConnection.getClass().getName(), "oracle.jdbc.driver.T4CConnection");
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

    @Test(invocationCount = /*200*/1 )
    public void testAllocateConnection_many() throws SQLException {
        testAllocateConnection();
    }

}
