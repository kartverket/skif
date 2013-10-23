package no.statkart.skif.store.endringslogg;

import no.statkart.skif.exception.ImplementationException;
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

    private I endretBubbleId;
    private Endringstype endringstype;
    private Timestamp endringstidspunkt;

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

    public BubbleId<?> getEndretBubbleId() {
        return endretBubbleId;
    }

    void setEndretBubbleId(I id) {
        if (endretBubbleId == null) {
            endretBubbleId = id;
        } else {
            throw new ImplementationException(String.format("Attempt to change immutable field %s", "endretBubbleId"));
        }
    }

}
