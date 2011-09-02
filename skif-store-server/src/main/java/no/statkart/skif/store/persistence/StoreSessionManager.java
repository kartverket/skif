package no.statkart.skif.store.persistence;

import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.store.ReplicaVersion;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSessionManager extends ConnectionManager {
    StoreSession getStoreSession(Object key) throws SQLException;
}
