package no.statkart.skif.store.endringslogg;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

import java.sql.Timestamp;

/**
 * Baseklasse for endringer.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class AbstractEndring<I extends BubbleId<?>> extends AbstractBubbleObject {
    private static final long serialVersionUID = 1L;

    private Endringstype endringstype;
    private Timestamp endringstidspunkt;
    private String brukernavn;

    @Override
    public AbstractEndringId<?> getId() {
        return (AbstractEndringId<?>) super.getId();
    }

    public long getEndringsnummer() {
        return getId().getValue();
    }

    public Endringstype getEndringstype() {
        return endringstype;
    }

    void setEndringstype(Endringstype endringstype) {
        this.endringstype = endringstype;
    }

    public Timestamp getEndringstidspunkt() {
        return endringstidspunkt;
    }

    public void setEndringstidspunkt(Timestamp endringstidspunkt) {
        this.endringstidspunkt = endringstidspunkt;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    void setBrukernavn(String brukernavn) {
        this.brukernavn = brukernavn;
    }

    public abstract BubbleId<?> getEndretBubbleId();

    void setEndretBubbleId(BubbleId<?> id) {
        setEndretBubbleIdImpl(id);
    }

    /**
     * Alle implementasjoner må implementere denne og koble den til sitt korrekt typede felt.
     *
     * @param id    id til boblen endringen gjelder
     */
    protected abstract void setEndretBubbleIdImpl(BubbleId<?> id);
}
