package no.statkart.skif.storetest.domain.multikobling;

import no.statkart.skif.store.multikobling.Kobling;

/**
 * Kobling for {@link Multirefererende}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class MultirefererendeKobling extends Kobling<String, String> {
    private static final long serialVersionUID = 1L;

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

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
}
