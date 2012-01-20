package no.statkart.skif.persistence.jdbc;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ConnectionFactory {
    Connection createConnection() throws SQLException;
    boolean isSnapshotChangable();
    boolean accepts(SnapshotVersion snapshotVersion);
    void setSnapshotVersion(Connection connection, SnapshotVersion snapshotVersion);
}
