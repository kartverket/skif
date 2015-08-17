package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.store.SnapshotVersion;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection factory for bruk av et fast pool. Tilsvarer {@link ConnectionFactoryUsingDataSource}, bortsett fra at
 * denne holder på en fast referanse fremfor å gjøre JNDI-lookup.
 *
 * @since 2.7.0
 */
public class ConnectionFactoryUsingPool extends AbstractConnectionFactory {
    private final DataSource pool;

    public ConnectionFactoryUsingPool(DataSource pool, boolean snapshotChangable, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        super(snapshotChangable, snapshotVersion, setSnapshotOnSession);
        this.pool = pool;
    }

    @Override
    public Connection createConnection() throws SQLException {
        return pool.getConnection();
    }
}
