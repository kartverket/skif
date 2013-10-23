package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * Baseklasse for alle endringer i StoreTest-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class Endring<I extends StoreTestBubbleId<?>> extends AbstractEndring<I> implements StoreTestBubble {
    private static final long serialVersionUID = 1L;

    private String brukernavn;



    public String getBrukernavn() {
        return brukernavn;
    }

    public void setBrukernavn(String brukernavn) {
        this.brukernavn = brukernavn;
    }

}
