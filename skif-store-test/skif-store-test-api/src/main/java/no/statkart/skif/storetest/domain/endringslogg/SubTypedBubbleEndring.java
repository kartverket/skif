package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.storetest.domain.basic.SubTypedBubbleId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.SubTypedBubble}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SubTypedBubbleEndring<I extends SubTypedBubbleEndringId<?>, EI extends SubTypedBubbleId<?>> extends Endring<I, EI> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("UnusedDeclaration") // Hibernate og AbstractEndringManager
    public SubTypedBubbleEndring() {
        super();
    }

    public SubTypedBubbleEndring(Long value) {
        setId(new SubTypedBubbleEndringId<>(value));
    }


    @Override
    public I getId() {
        return super.getId();
    }

    @Override
    public EI getEndretBubbleId() {
        return super.getEndretBubbleId();
    }

}
