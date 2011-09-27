package no.statkart.skif.store2;


/**
 * Interface for å velge hvilken {@link no.statkart.skif.store.StorePersister} som skal brukes basert på {@link AbstractBubbleId2}
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StorePersisterStrategy2 {
    /**
     * Returnerer StorePersister instans som skal brukes for gitt BubbleId. Dersom kun en StorePersister er i bruk
     * returneres samme instans for alle BubbleId objketer.
     * @param bubbleId id som skal klassifiseres
     * @return  StorePersister som skal brukes
     */
    StorePersister2 getPersister(BubbleId2 bubbleId);
}
