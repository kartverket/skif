package no.statkart.skif.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public interface WrappableStoreSession5 extends StoreSession5 {
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 getEntry(int level, I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry5> getEntries(int level, Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 loadEntry(int level, I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry5> loadEntries(int level, Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId);
    <T extends BubbleObject> StoreEntry5 registerEntry(int level, T bubbleObject);
    <T extends BubbleObject> StoreEntry5 registerLockedEntry(int level, T bubbleObject);
    void registerTransfer(int level, UnitOfWorkTransfer transfer);
    <T extends BubbleObject> StoreEntry5 insertEntry(int level, T bubbleObject);
    <T extends BubbleObject> StoreEntry5 updateEntry(int level, T bubbleObject);
    <T extends BubbleObject> StoreEntry5 deleteEntry(int level, T bubbleObject);

    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry5 lockEntry(int level, I bubbleId);


    <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);

    <T extends BubbleObject, I extends BubbleId<? extends T>>  Map<I,List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);
}

