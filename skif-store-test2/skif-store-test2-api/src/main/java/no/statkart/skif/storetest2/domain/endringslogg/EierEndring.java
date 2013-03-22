package no.statkart.skif.storetest2.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest2.domain.eierskap.EierId;

/**
 * Endring for {@link no.statkart.skif.storetest2.domain.eierskap.Eier}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EierEndring<I extends EierId<?>> extends Endring<I> {
    private static final long serialVersionUID = 1L;

    private EierId<?> eierId;

    @Override
    public EierEndringId<?> getId() {
        return (EierEndringId<?>) super.getId();
    }

    @Override
    public EierId<?> getEndretBubbleId() {
        return getEierId();
    }

    @Override
    protected void setEndretBubbleIdImpl(BubbleId<?> id) {
        setEierId((EierId<?>) id);
    }

    public EierId<?> getEierId() {
        return eierId;
    }

    void setEierId(EierId<?> eierId) {
        this.eierId = eierId;
    }
}
