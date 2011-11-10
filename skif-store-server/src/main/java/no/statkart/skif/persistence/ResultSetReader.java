package no.statkart.skif.persistence;

import no.statkart.skif.store.SnapshotVersion;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface ResultSetReader {
    void readResult(final ResultSet resultSet, final SnapshotVersion snapshotVersion) throws SQLException;
}
