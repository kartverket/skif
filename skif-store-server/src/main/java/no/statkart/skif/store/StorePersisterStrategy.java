package no.statkart.skif.store;

import no.statkart.skif.store.persistence.StoreSession;

/**
 * Interface for å velge hvilken {@link StoreSession} som skal brukes basert på {@link AbstractBubbleId}
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StorePersisterStrategy {
    /**
     * Returnerer StoreSession instans som skal brukes for gitt BubbleId. Dersom kun en StoreSession er i bruk
     * returneres samme instans for alle BubbleId objketer.
     * @param bubbleId id som skal klassifiseres
     * @return  StorePersister som skal brukes
     */
    StoreSession getPersister(BubbleId bubbleId);
}
