package no.statkart.skif.store2;

import no.statkart.skif.store2.*;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public abstract class AbstractStoreSessionAuthorizerChain2 extends AbstractStoreSessionReadChain2 implements StoreSessionAuthorizerChain2 {

    @Override
    public void clear() {
        // No-op
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId) {
        StoreEntry2<T> entry= nextInReadChain.get(bubbleId);
        maskFields(entry);
        return entry;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> get(Collection<I> bubbleIds) {
        Collection<StoreEntry2<T>> entries = nextInReadChain.get(bubbleIds);
        for (StoreEntry2<T> entry : entries) {
            maskFields(entry);
        }
        return entries;
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> register(T bubbleObject) {
        StoreEntry2<T> entry= nextInReadChain.register(bubbleObject);
        if (bubbleObject == entry.bubbleObject) {
            // Objektet er nytt eller har erstattet opprindelig objekt i cachen. Masker felter om nødvendig.
            maskFields(entry);
        }
        return entry;
    }

}
