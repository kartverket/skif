package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSession {
    <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId);

}
