package no.statkart.skif.store.persistence.jdbc;

import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class ConnectionManagerWithStrategy implements ConnectionManager {
    @Override
    public ConnectionForSnapshotVersion getForSnapshotVersion(SnapshotVersion snapshotVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
