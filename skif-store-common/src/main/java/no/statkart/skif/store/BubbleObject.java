package no.statkart.skif.store;

import no.statkart.skif.bubble.spi.SkifBubbleObject;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleObject extends SkifBubbleObject {

    /**
     * Gir tilbake bubbleId for dette bubbleObject og skal implementeres på det høyeste nivået for å gi en så generell id
     * som mulig.
     * <p/>
     * Denne funksjonen brukes fra HibernateStoreInterceptor for å kunne gi id-er riktige subtyper ved henting fra databasen.
     *
     * @return Id for objektet med generell type
     */
    BubbleId<?> getBubbleId();

    BubbleId<?> getId();

    void setId(BubbleId<?> id);

    Store store();

    void register(Store store);

    /**
     * SKIF internal use only!
     */
    void setFlushed(boolean flushed);

    boolean isFlushed();

}
