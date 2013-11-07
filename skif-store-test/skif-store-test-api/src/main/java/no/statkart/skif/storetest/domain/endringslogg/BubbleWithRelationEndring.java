package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.BubbleWithRelation}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class BubbleWithRelationEndring<I extends BubbleWithRelationEndringId<?>, EI extends BubbleWithRelationId<?>> extends Endring<I, EI> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("UnusedDeclaration") // Hibernate og AbstractEndringManager
    public BubbleWithRelationEndring() {
        super();
    }

    public BubbleWithRelationEndring(Long value) {
        setId(new BubbleWithRelationEndringId<BubbleWithRelationEndring>(value));
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
