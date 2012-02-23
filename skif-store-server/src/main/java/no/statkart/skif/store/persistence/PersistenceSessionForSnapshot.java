package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;

import java.util.Collection;

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
