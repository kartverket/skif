package no.statkart.skif.storetest2.domain.endringslogg;

import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.storetest2.domain.StoreTest2Bubble;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * Baseklasse for alle endringer i StoreTest-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class Endring<I extends StoreTest2BubbleId<?>> extends AbstractEndring<I> implements StoreTest2Bubble {
    private static final long serialVersionUID = 1L;
}
