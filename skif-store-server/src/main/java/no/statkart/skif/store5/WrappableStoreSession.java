package no.statkart.skif.store5;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface WrappableStoreSession extends StoreSession {
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry getEntry(int level, I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> getEntries(int level, Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId);
    <T extends BubbleObject> StoreEntry registerEntry(int level, T bubbleObject);
    <T extends BubbleObject> StoreEntry registerLockedEntry(int level, T bubbleObject);
    void registerTransfer(int level, UnitOfWorkTransfer transfer);
    <T extends BubbleObject> StoreEntry insertEntry(int level, T bubbleObject);
    <T extends BubbleObject> StoreEntry updateEntry(int level, T bubbleObject);
    <T extends BubbleObject> StoreEntry deleteEntry(int level, T bubbleObject);

    <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId);


}

