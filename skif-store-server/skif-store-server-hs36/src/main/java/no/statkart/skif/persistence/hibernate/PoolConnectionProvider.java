package no.statkart.skif.persistence.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.DatasourceConnectionProvider;
import org.hibernate.util.PropertiesHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Hibernate har kun innebygget støtte for å hente DataSource fra JNDI. Denne utvider dette til å kunne få en DataSource
 * angitt eksplisitt.
 */
public class PoolConnectionProvider extends DatasourceConnectionProvider {
    private boolean autoCommit;

    @Override
    public void configure(Properties props) throws HibernateException {
        DataSource dataSource = (DataSource) props.get(Environment.DATASOURCE);
        if (dataSource == null) {
            throw new HibernateException("No DataSource");
        }
        setDataSource(dataSource);

        autoCommit = PropertiesHelper.getBoolean(Environment.AUTOCOMMIT, props);
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
