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
public abstract class AbstractEndring<I extends AbstractEndringId<?>, EI extends BubbleId<?>> extends AbstractBubbleObject {
    private static final long serialVersionUID = 1L;

    private EI endretBubbleId;
    private Endringstype endringstype;
    @Deprecated
    private Timestamp endringstidspunkt;

    @Override
    @SuppressWarnings("unchecked")
   public I getId() {
        return (I) super.getId();
    }

    public Endringstype getEndringstype() {
        return endringstype;
    }

    public void setEndringstype(Endringstype endringstype) {
        this.endringstype = endringstype;
    }

    public Timestamp getEndringstidspunkt() {
        return endringstidspunkt;
    }

    public void setEndringstidspunkt(Timestamp endringstidspunkt) {
        this.endringstidspunkt = endringstidspunkt;
    }

    public EI getEndretBubbleId() {
        return endretBubbleId;
    }

    public void setEndretBubbleId(EI id) {
        if (endretBubbleId == null) {
            endretBubbleId = id;
        } else {
            throw new ImplementationException(String.format("Attempt to change immutable field %s", "endretBubbleId"));
        }
    }

}
