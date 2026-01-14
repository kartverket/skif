package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
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
        EndringId<?> id = super.getId();
        if (id == null) {
            return null;
        }
        if (id instanceof BubbleWithRelationEndringId) {
            @SuppressWarnings("unchecked")
            I typedId = (I) id;
            return typedId;
        }
        @SuppressWarnings("unchecked")
        I typedId = (I) new BubbleWithRelationEndringId<>(id.getValue(), id.getSnapshotVersion());
        return typedId;
    }

    @Override
    public EI getEndretBubbleId() {
        BubbleId<?> id = super.getEndretBubbleId();
        if (id == null) {
            return null;
        }
        if (id instanceof BubbleWithRelationId) {
            return (EI) id;
        }
        return (EI) new BubbleWithRelationId<>((Long) id.getValue(), id.getSnapshotVersion());
    }

}
