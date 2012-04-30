package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

public class Matrikkelenhet extends AbstractStoreTestBubble {
    private KommuneId<?> kommuneId;
    private int gaardsnummer;
    private int bruksnummer;
    private int festenummer;
    private int seksjonsnummer;

    @Override
    public MatrikkelenhetId<?> getId() {
        return (MatrikkelenhetId<?>) super.getId();
    }

    public KommuneId<?> getKommuneId() {
        return kommuneId;
    }

    public void setKommuneId(KommuneId<?> kommuneId) {
        this.kommuneId = kommuneId;
    }

    public int getGaardsnummer() {
        return gaardsnummer;
    }

    public void setGaardsnummer(int gaardsnummer) {
        this.gaardsnummer = gaardsnummer;
    }

    public int getBruksnummer() {
        return bruksnummer;
    }

    public void setBruksnummer(int bruksnummer) {
        this.bruksnummer = bruksnummer;
    }

    public int getFestenummer() {
        return festenummer;
    }

    public void setFestenummer(int festenummer) {
        this.festenummer = festenummer;
    }

    public int getSeksjonsnummer() {
        return seksjonsnummer;
    }

    public void setSeksjonsnummer(int seksjonsnummer) {
        this.seksjonsnummer = seksjonsnummer;
    }
}
