package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class DataSourceConnectionFactory implements ConnectionFactory {
    private final String dataSourceName;

    public DataSourceConnectionFactory(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Connection createConnection() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(dataSourceName);
            Connection con = ds.getConnection();
            return con;
        } catch (NamingException e) {
            throw new OperationalException("Unknown datasource: " + dataSourceName, e);
        } catch (SQLException e) {
            throw new OperationalException("Could not create creation on datasource: " + dataSourceName, e);
        }
    }
}
