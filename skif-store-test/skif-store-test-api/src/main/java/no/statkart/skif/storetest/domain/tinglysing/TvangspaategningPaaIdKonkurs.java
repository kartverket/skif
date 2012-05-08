package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Oddbjørn Kvalsund
 */
public class TvangspaategningPaaIdKonkurs extends TvangspaategningPaaId {
    private String saksnummer;
    private String bobestyrer;

    public String getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(String saksnummer) {
        this.saksnummer = saksnummer;
    }

    public String getBobestyrer() {
        return bobestyrer;
    }

    public void setBobestyrer(String bobestyrer) {
        this.bobestyrer = bobestyrer;
    }
}
