package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Baseklasse for alle kontroll objekter i StoreTest-prosjektet.
 *
 * @author Henrik Fredholm
 * @since 2.5.0
 */
public class Kontroll implements Serializable {
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