package no.statkart.skif.wsversioning.domain;

import no.statkart.skif.util.Since;

/**
 * Eksempelklasse som tidligere het Gate, basert på et faktisk tilfelle.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class Veg extends AbstractWSVersioningBubbleObject {
    private static final long serialVersionUID = 1L;

    private String adressenavn;

    @Since("2.1")
    private String alternativtNavn;

    public Veg() {
    }

    public Veg(VegId<?> id, String adressenavn, String alternativtNavn) {
        setId(id);
        this.adressenavn = adressenavn;
        this.alternativtNavn = alternativtNavn;
    }

    @Override
    public VegId<?> getId() {
        return (VegId<?>) super.getId();
    }

    public String getAdressenavn() {
        return adressenavn;
    }

    public void setAdressenavn(String adressenavn) {
        this.adressenavn = adressenavn;
    }

    public String getAlternativtNavn() {
        return alternativtNavn;
    }

    public void setAlternativtNavn(String alternativtNavn) {
        this.alternativtNavn = alternativtNavn;
    }
}
