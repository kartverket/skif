package no.statkart.skif.store5.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionForSnapshot extends PersistenceSession {
    SnapshotVersion getSnapshot();
    SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion);
    boolean isSnapshotChangable();
    boolean acceptsSnapshot(SnapshotVersion snapshotVersion);
    PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type);
    <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType);
}
