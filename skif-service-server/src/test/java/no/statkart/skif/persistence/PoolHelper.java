package no.statkart.skif.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

public class PoolHelper {
    public static DataSource createPooledDataSource(Configuration configuration) {
        String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
        String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
        String url = configuration.getString(SkifConfigConstants.DB_JDBC_URL);

        try {
            ComboPooledDataSource pool = new ComboPooledDataSource();
            pool.setDriverClass("oracle.jdbc.OracleDriver");
            pool.setJdbcUrl(url);
            pool.setUser(username);
            pool.setPassword(password);
            return pool;
        } catch (PropertyVetoException e) {
            throw new ImplementationException("Could not set up connection pool", e);
        }
    }
}
