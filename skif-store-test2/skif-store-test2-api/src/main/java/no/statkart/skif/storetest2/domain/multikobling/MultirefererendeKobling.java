package no.statkart.skif.storetest2.domain.multikobling;

import no.statkart.skif.store.multikobling.Kobling;

/**
 * Kobling for {@link Multirefererende}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class MultirefererendeKobling extends Kobling<String, String> {
    private String tekst;

    public MultirefererendeKobling() {
    }

    public MultirefererendeKobling(String rolle, String value) {
        super(rolle, value);
    }

    @Override
    protected String getValue() {
        return getTekst();
    }

    @Override
    protected void setValue(String value) {
        setTekst(value);
    }

    // Nødvendig for bruk i mapping med Hibernate 3.2. Den tror getRolle() returnerer Object, siden den finner bridge metoden.
    private String getRolleWorkaround() {
        return getRolle();
    }

    // Nødvendig for bruk i mapping med Hibernate 3.2. Den tror setRolle() tar inn Object, siden den finner bridge metoden.
    private void setRolleWorkaround(String rolle) {
        setRolle(rolle);
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
}
