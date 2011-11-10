package no.statkart.skif.store;


/**
 * Interface for å velge hvilken {@link no.statkart.skif.store.StorePersister} som skal brukes basert på {@link no.statkart.skif.store.AbstractBubbleId}
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StorePersisterStrategy {
    /**
     * Returnerer StorePersister instans som skal brukes for gitt BubbleId. Dersom kun en StorePersister er i bruk
     * returneres samme instans for alle BubbleId objketer.
     * @param bubbleId id som skal klassifiseres
     * @return  StorePersister som skal brukes
     */
    StorePersister getPersister(BubbleId bubbleId);
}
