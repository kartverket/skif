package no.statkart.skif.service.sequence;

import no.statkart.skif.store.BubbleId;

/**
 * Interface for utgivelse av unike id-verdier en om gangen fra en blokk av allerede allokert sekvensnumre. Når
 * sekvensblokken er brukt opp allokeres en ny sekvensblokk ved kall til {@link SequenceBlockAllocatorService}.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface IdService {

   /**
    * Returnere neste frie id-verdi for gitt klasse.
    * <p>
    * Det er implementasjonsspesifikk om samme sekvens vil bli brukt for alle klasser eller om klasser vil ha egne
    * sekvenser.
    *
    * @param idClass klasse det skal allokeres id-verdi for
    * @return neste ubruke id-verdi for klassen
    */
   public <T extends BubbleId<?>> Object getNextIdValue(Class<T> idClass);

   /**
    * Oppretter et nytt BubbleId objekt med neste fri id-verdi for klassen.
    *
    * @param idClass klasse det skal allokeres id-verdi for
    */
   public <T extends BubbleId<?>> T getNextId(Class<T> idClass);

    /**
     * Returnere blokstørrelse som vil bli brukt neste gang det skal allokeres en ny sekvensblokk
     * @return antall id'er som vil blir allokert
     */
    public int getBlockSize();


   /**
    * Setter blokstørrelse som vil bli brukt neste gang det skal allokeres en ny sekvensblokk
    * @param blockSize antall id'er som vil blir allokert
    */
   public void setBlockSize(int blockSize);

   /**
    * Nullstiller cachet sekvens. Neste kall til {@link #getNextIdValue} vil allokere en ny sekvensblokk. Ubrukte
    * sekvensnumre vil gå tabt.
    */
   public void clear();
}
