package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSession {
    <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId);
    <T extends BubbleObject> void ensureFullyLoaded(T bubble);
    <T extends BubbleObject> T refresh(BubbleId<? extends T> bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> refresh(Collection<I> bubbleIds);
    <T extends BubbleObject> void refresh(T bubble);

}
