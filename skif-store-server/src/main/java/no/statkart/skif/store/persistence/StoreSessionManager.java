package no.statkart.skif.store.persistence;

import no.statkart.skif.store.ReplicaVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSessionManager {
    StoreSession getSession(ReplicaVersion replicaVersion);
    boolean hasActiveSession(ReplicaVersion replicaVersion);
    void flushSession(ReplicaVersion replicaVersion);
    void flushAllSessions();
    void closeSession(ReplicaVersion replicaVersion);
    void closeAllSessions();
}
