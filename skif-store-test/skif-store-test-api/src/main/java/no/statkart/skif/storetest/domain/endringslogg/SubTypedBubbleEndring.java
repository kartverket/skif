package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
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
        setId(new SubTypedBubbleEndringId<SubTypedBubbleEndring>(value));
    }


    @Override
    public I getId() {
        EndringId<?> id = super.getId();
        if (id == null) {
            return null;
        }
        if (id instanceof SubTypedBubbleEndringId) {
            @SuppressWarnings("unchecked")
            I typedId = (I) id;
            return typedId;
        }
        @SuppressWarnings("unchecked")
        I typedId = (I) new SubTypedBubbleEndringId<>(id.getValue(), id.getSnapshotVersion());
        return typedId;
    }

    @Override
    public EI getEndretBubbleId() {
        BubbleId<?> id = super.getEndretBubbleId();
        if (id == null) {
            return null;
        }
        if (id instanceof SubTypedBubbleId) {
            return (EI) id;
        }
        return (EI) new SubTypedBubbleId<>((Long) id.getValue(), id.getSnapshotVersion());
    }

}
