package no.statkart.skif.store.persistence;

import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.store.ReplicaVersion;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSessionManager extends ConnectionManager {
    StoreSession getStoreSession(Object key);
}
