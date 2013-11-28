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
public class Kontroll extends AbstractKontroll {
    private static final long serialVersionUID = 1L;
    private long antall;
    private long idChecksum;

    public long getAntall() {
        return antall;
    }

    public void setAntall(long antall) {
        this.antall = antall;
    }

    public long getIdChecksum() {
        return idChecksum;
    }

    public void setIdChecksum(long idChecksum) {
        this.idChecksum = idChecksum;
    }
}
