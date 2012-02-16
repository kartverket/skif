package no.statkart.skif.persistence5;

import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.persistence.jdbc.ConnectionManagerUsingFactory;
import no.statkart.skif.store.SnapshotVersion;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static no.statkart.skif.util.JDBCHelper.createConnectionFactoryUsingJDBC;
import static org.testng.Assert.assertFalse;

/**
 * Tester for {@link ResourceManager} som styre connections opprettet via JDBC factories
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class ResourceManagerUsingJDBCOnlyTest {

    public void test() throws SQLException {

        SkifConfiguration config = new SkifConfiguration();
        ConnectionManagerUsingFactory managerUsingFactory = new ConnectionManagerUsingFactory(
                createConnectionFactoryUsingJDBC(config, SnapshotVersion.CURRENT, false),
                createConnectionFactoryUsingJDBC(config, SnapshotVersion.OLD, false)
        );

        ResourceManager resourceManager = new DefaultResourceManager(
                new ResourceManager.Entry(managerUsingFactory, ConnectionManager.class)
        );


        ConnectionManager connectionManager = resourceManager.getResource(ConnectionManager.class);

        Connection forSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(forSnapshotVersion.getAutoCommit());
        resourceManager.close();
    }
}
