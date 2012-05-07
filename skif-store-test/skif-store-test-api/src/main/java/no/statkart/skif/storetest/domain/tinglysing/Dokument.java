package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @since 2.1
 */
public class Dokument extends AbstractStoreTestBubble {
    private int dokumentaar;
    private int dokumentnummer;
    private String embete; // TODO: enum? Kode?
    private String status;

    @Override
    public DokumentId<?> getId() {
        return (DokumentId<?>) super.getId();
    }

    public int getDokumentaar() {
        return dokumentaar;
    }

    public void setDokumentaar(int dokumentaar) {
        this.dokumentaar = dokumentaar;
    }

    public int getDokumentnummer() {
        return dokumentnummer;
    }

    public void setDokumentnummer(int dokumentnummer) {
        this.dokumentnummer = dokumentnummer;
    }

    public String getEmbete() {
        return embete;
    }

    public void setEmbete(String embete) {
        this.embete = embete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
