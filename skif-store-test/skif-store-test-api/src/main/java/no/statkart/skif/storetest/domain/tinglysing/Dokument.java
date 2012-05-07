package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @author rorchr
 */
public class Dokument extends AbstractStoreTestBubble {
    private int dokumentaar;
    private int dokumentnummer;
    private EmbeteId<?> embeteId;
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

    public EmbeteId<?> getEmbeteId() {
        return embeteId;
    }

    public void setEmbeteId(EmbeteId<?> embeteId) {
        this.embeteId = embeteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
