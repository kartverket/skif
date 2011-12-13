package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Collection;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceManager extends TransactionalResource {
    <T extends BubbleObject, I extends BubbleId<? extends T>> SnapshotManagedPersistenceSession<?> getPersistenceManager(I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Map<SnapshotVersion, Map<SnapshotManagedPersistenceSession<?>, Collection<I>>> getPersistenceManagers(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);
}
