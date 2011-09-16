package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public abstract class AbstractStoreSessionAuthorizerChain extends AbstractStoreSessionReadChain implements StoreSessionAuthorizerChain {

    @Override
    public void clear() {
        // No-op
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        StoreEntry<T> entry= nextInReadChain.get(bubbleId);
        maskFields(entry);
        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        Collection<StoreEntry<T>> entries = nextInReadChain.get(bubbleIds);
        for (StoreEntry<T> entry : entries) {
            maskFields(entry);
        }
        return entries;
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject) {
        StoreEntry<T> entry= nextInReadChain.register(bubbleObject);
        if (bubbleObject == entry.bubbleObject) {
            // Objektet er nytt eller har erstattet opprindelig objekt i cachen. Masker felter om nødvendig.
            maskFields(entry);
        }
        return entry;
    }

}
