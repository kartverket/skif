package no.statkart.skif.store5.persistence.jdbc;

import no.statkart.skif.persistence5.jdbc.ConnectionManager;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class ConnectionManagerWithStrategy implements ConnectionManager {
    @Override
    public Connection getForSnapshotVersion(SnapshotVersion snapshotVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
