package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.store.SnapshotVersion;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionFactoryUsingDataSource extends AbstractConnectionFactory {
    private final String dataSourceName;

    public ConnectionFactoryUsingDataSource(String dataSourceName, boolean snapshotChangable, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        super(snapshotChangable, snapshotVersion, setSnapshotOnSession);
        this.dataSourceName = dataSourceName;
    }

    public Connection createConnection() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(dataSourceName);
            return ds.getConnection();
        } catch (NamingException e) {
            throw new OperationalException("Unknown datasource: " + dataSourceName, e);
        } catch (SQLException e) {
            throw new OperationalException("Could not create creation on datasource: " + dataSourceName, e);
        }
    }
}
