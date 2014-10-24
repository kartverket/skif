package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleObject extends Serializable {
    /**
     * Gir tilbake bubbleId for dette bubbleObject og skal implementeres på det høyeste nivået for å gi en så generell id
     * som mulig.
     *
     * Denne funksjonen brukes fra HibernateStoreInterceptor for å kunne gi id-er riktige subtyper ved henting fra databasen.
     *
     * @return Id for objektet med generell type
     */
    public BubbleId<?> getBubbleId();
    public BubbleId<?> getId();
    public void setId(BubbleId<?> id);
    public Store store();
    public void register(Store store);
    public long getVersjonId();
    public void setVersjonId(long version);

    /**
     * SKIF internal use only!
     */
    void setFlushed(boolean flushed);
    boolean isFlushed();
}
