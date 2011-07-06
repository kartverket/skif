package no.statkart.skif.store.persistence;

import no.statkart.skif.store.ReplicaVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSessionManager {
    StoreSession getSession(ReplicaVersion replicaVersion);
    boolean isSessionAllocated(ReplicaVersion replicaVersion);
    void flushSession(ReplicaVersion replicaVersion);
    void flushAllSessions();
    void releaseSession(ReplicaVersion replicaVersion);
    void releaseAllSessions();
}
