package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractKontroll;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * Baseklasse for alle kontroll objekter i StoreTest-prosjektet.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Kontroll<I extends StoreTestBubbleId<?>> extends AbstractKontroll<I> {
    private static final long serialVersionUID = 1L;
    private long antall;

    public long getAntall() {
        return antall;
    }

    public void setAntall(long antall) {
        this.antall = antall;
    }
}
