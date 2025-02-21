package no.statkart.skif.persistence.hibernate;

import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.internal.util.config.ConfigurationHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Hibernate har kun innebygget støtte for å hente DataSource fra JNDI. Denne utvider dette til å kunne få en DataSource
 * angitt eksplisitt.
 */
public class PoolConnectionProvider extends DatasourceConnectionProviderImpl {
    private boolean autoCommit;

    @Override
    public void configure(Map configValues) {
        super.configure(configValues);
        autoCommit = ConfigurationHelper.getBoolean(Environment.AUTOCOMMIT, configValues);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        // Dette er tilsvarende kode i DriverManagerConnectionProvider
        if (connection.getAutoCommit() != autoCommit) {
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }
}
