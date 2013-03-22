package no.statkart.skif.storetest2.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest2.domain.eierskap.EiendomId;

/**
 * Endring for {@link no.statkart.skif.storetest2.domain.eierskap.Eiendom}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EiendomEndring<I extends EiendomId<?>> extends Endring<I> {
    private static final long serialVersionUID = 1L;

    private EiendomId<?> eiendomId;

    @Override
    public EiendomEndringId<?> getId() {
        return (EiendomEndringId<?>) super.getId();
    }

    @Override
    public EiendomId<?> getEndretBubbleId() {
        return getEiendomId();
    }

    @Override
    protected void setEndretBubbleIdImpl(BubbleId<?> id) {
        setEiendomId((EiendomId<?>) id);
    }

    public EiendomId<?> getEiendomId() {
        return eiendomId;
    }

    void setEiendomId(EiendomId<?> eiendomId) {
        this.eiendomId = eiendomId;
    }
}
