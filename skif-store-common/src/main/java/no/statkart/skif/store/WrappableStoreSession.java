package no.statkart.skif.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface WrappableStoreSession extends StoreSession {

    <T extends BubbleObject> StoreEntry insertEntry(int level, T bubbleObject);

    <T extends BubbleObject> StoreEntry updateEntry(int level, T bubbleObject);

    <T extends BubbleObject> StoreEntry deleteEntry(int level, T bubbleObject);

    <T extends BubbleObject> boolean undoEntry(int level, T bubbleObject);

    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntriesIgnoreMissing(int level, Set<I> bubbleIds, boolean refresh);

    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> lockEntries(int level, Set<I> bubbleIds);

    StoreEntry unlockEntry(int level, BubbleId<?> bubbleId);

    Collection<StoreEntry> unlockEntries(int level, Collection<? extends BubbleId<?>> bubbleIds);

    boolean evictEntry(int level, BubbleId<?> bubbleId);

    boolean evictAllEntries(int level);


    Collection<StoreEntry> registerEntries(int level, Transfer<?> transfer);


    StoreUnitOfWork beginUnitOfWork();

    void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified);


    <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end);

    int getLevel();

    boolean inAttachedMode();

    /**
     * Returnerer et BubbleObject med samme innhold som databasen eller null dersom objektet er nytt. Metoden
     * er kun implementert for objekter som allerede er låst (kan vurdere å utvidet hvis det er et behov for det)
     */
    BubbleObject getPersistedBubbleObjectForLocked(StoreEntry storeEntry);

}

