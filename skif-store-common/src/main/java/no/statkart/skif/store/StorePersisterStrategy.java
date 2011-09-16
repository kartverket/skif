package no.statkart.skif.store;

/**
 * Interface for å velge hvilken {@link StorePersister} som skal brukes basert på {@link AbstractBubbleId}
 * @author Henrik Fredholm
 * @since 0.3
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
