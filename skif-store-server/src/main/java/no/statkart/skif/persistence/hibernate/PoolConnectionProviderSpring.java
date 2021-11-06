package no.statkart.skif.persistence.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Hibernate har kun innebygget støtte for å hente DataSource fra JNDI. Denne utvider dette til å kunne få en DataSource
 * angitt eksplisitt og som bruker Springs DataSourceUtils til å hente ut connections.
 */
public class PoolConnectionProviderSpring extends DatasourceConnectionProviderImpl {
    private boolean autoCommit;
    private boolean available;

    @Override
    public void configure(Map configValues) {
        super.configure(configValues);
        available = true;
        autoCommit = ConfigurationHelper.getBoolean(Environment.AUTOCOMMIT, configValues);
    }

    @Override
    public void stop() {
        super.stop();
        available = false;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if ( !available ) {
            throw new HibernateException( "Provider is closed!" );
        }
        Connection connection = DataSourceUtils.getConnection(getDataSource());
        // Dette er tilsvarende kode i DriverManagerConnectionProvider
        if (connection.getAutoCommit() != autoCommit) {
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        DataSourceUtils.doReleaseConnection(connection, getDataSource());
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }
}
