package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.Simple}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SimpleEndring<I extends SimpleEndringId<?>, EI extends SimpleId<?>> extends Endring<I, EI> {
    private static final long serialVersionUID = 1L;


    @Override
    public I getId() {
        return super.getId();
    }

    @Override
    public EI getEndretBubbleId() {
        return super.getEndretBubbleId();
    }

}
