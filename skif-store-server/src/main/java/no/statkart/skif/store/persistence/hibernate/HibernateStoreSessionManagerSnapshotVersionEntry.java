package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Henrik Fredholm
 */
class HibernateStoreSessionManagerSnapshotVersionEntry extends HibernateSessionManagerEntry {
    HibernateStoreSession storeSession;
    Deque<SnapshotVersion> snapshotVersionStack = new ArrayDeque<SnapshotVersion>();

    HibernateStoreSessionManagerSnapshotVersionEntry(SnapshotVersionSeed snapshotVersionSeed) {
        super(snapshotVersionSeed);
    }

    public void pushSnapshotVersion(SnapshotVersion snapshotVersion) {
        snapshotVersionStack.push(snapshotVersion);
        setSnapshotVersion(snapshotVersion);
    }

    public void setSnapshotVersion(SnapshotVersion snapshotVersion) {
        SnapshotVersion prev = getSnapshotVersion();
        ((SnapshotVersionSeed)key).set(snapshotVersion);
        if (!snapshotVersion.equals(prev)) {
            // TODO set snapshot on session
        }
    }

    public SnapshotVersion getSnapshotVersion() {
        return ((SnapshotVersionSeed)key).get();
    }
}
