package no.statkart.skif.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface WrappableStoreSession extends StoreSession {

    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry insertEntry(int level, T bubbleObject);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry updateEntry(int level, T bubbleObject);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry deleteEntry(int level, T bubbleObject);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry  unlockEntry(int level, I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId);

    void registerEntries(int level, BubbleTransfer bubbleTransfer);

    StoreUnitOfWork beginUnitOfWork();
    void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified);

    <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);
    <T extends BubbleObject, I extends BubbleId<? extends T>>  Map<I,List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);
}

