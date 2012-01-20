package no.statkart.skif.persistence5.jdbc;


import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionForSnapshotVersionProvider implements Provider<ConnectionForSnapshotVersion> {
    private final ConnectionManager connectionManager;

    @Inject
    public ConnectionForSnapshotVersionProvider(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ConnectionForSnapshotVersion get() {
        return connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
    }
}
