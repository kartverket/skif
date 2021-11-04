package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.store.SnapshotVersion;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection factory for bruk av et fast pool. Tilsvarer {@link ConnectionFactoryUsingDataSource}, bortsett fra at
 * denne holder på en fast referanse fremfor å gjøre JNDI-lookup. Denne implementasjonen henter ut connections
 * via DataSourceUtil slik at den er kompatibel med Spring's DataSourceTransactionManager.
 *
 */
public class ConnectionFactoryUsingPoolSpring extends AbstractConnectionFactory {
    private final DataSource pool;

    public ConnectionFactoryUsingPoolSpring(DataSource pool, boolean snapshotChangable, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        super(snapshotChangable, snapshotVersion, setSnapshotOnSession);
        this.pool = pool;
    }

    @Override
    public Connection createConnection() throws SQLException {
        return DataSourceUtils.getConnection(pool);
    }

    @Override
    public void close(Connection connection) throws SQLException {
        DataSourceUtils.releaseConnection(connection, pool);
    }
}
