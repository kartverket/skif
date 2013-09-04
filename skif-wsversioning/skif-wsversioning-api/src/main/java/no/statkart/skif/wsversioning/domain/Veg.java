package no.statkart.skif.wsversioning.domain;

/**
 * Eksempelklasse som tidligere het Gate, basert på et faktisk tilfelle.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class Veg extends AbstractWSVersioningBubbleObject {
    private static final long serialVersionUID = 1L;

    private String adressenavn;

    public Veg() {
    }

    public Veg(VegId<?> id, String adressenavn) {
        setId(id);
        this.adressenavn = adressenavn;
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
}
