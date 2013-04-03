package no.statkart.skif.store.endringslogg;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

import java.util.Date;

/**
 * Baseklasse for endringer.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class AbstractEndring<I extends BubbleId<?>> extends AbstractBubbleObject {
    private static final long serialVersionUID = 1L;

    private int endringstype;
    private Date endringstidspunkt;
    private String brukernavn;

    @Override
    public AbstractEndringId<?> getId() {
        return (AbstractEndringId<?>) super.getId();
    }

    public long getEndringsnummer() {
        return getId().getValue();
    }

    public int getEndringstype() {
        return endringstype;
    }

    void setEndringstype(int endringstype) {
        this.endringstype = endringstype;
    }

    public Date getEndringstidspunkt() {
        return endringstidspunkt;
    }

    void setEndringstidspunkt(Date endringstidspunkt) {
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
