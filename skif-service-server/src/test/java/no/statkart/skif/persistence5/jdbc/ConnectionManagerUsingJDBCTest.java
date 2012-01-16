package no.statkart.skif.persistence5.jdbc;

import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static no.statkart.skif.util.JDBCHelper.createConnectionFactoryUsingJDBC;
import static org.testng.Assert.*;

/**
 * Tester for {@link ConnectionManagerUsingFactory}
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class ConnectionManagerUsingJDBCTest {
    private ConnectionManagerUsingFactory connectionManager;

    @BeforeClass
    public void setUp() {
        SkifConfiguration config = new SkifConfiguration();
         connectionManager = new ConnectionManagerUsingFactory(
                createConnectionFactoryUsingJDBC(config, SnapshotVersion.CURRENT, false),
                createConnectionFactoryUsingJDBC(config, SnapshotVersion.OLD, false)
        );
    }

    @AfterClass
    void tearDown() {
        connectionManager.close();
    }

    public void testAllocateConnection() throws SQLException {

        ConnectionForSnapshotVersion connectionForSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(connectionForSnapshotVersion.getAutoCommit());

        assertSame(connectionForSnapshotVersion, connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT));

        try {
            Connection unwrappedConnection = connectionForSnapshotVersion.reserve();
            assertEquals(unwrappedConnection.getClass().getName(), "oracle.jdbc.driver.T4CConnection");
        } finally {
            connectionForSnapshotVersion.release();
        }

        Connection connectionOld = connectionManager.getForSnapshotVersion(SnapshotVersion.OLD);
        assertFalse(connectionForSnapshotVersion.getAutoCommit());

        assertNotSame(connectionForSnapshotVersion, connectionOld);
        connectionManager.close();
    }

    @Test(invocationCount = 200)
    public void testAllocateConnection_many() throws SQLException {
        testAllocateConnection();
    }


}
