package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.Simple}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SimpleEndring<I extends SimpleEndringId<?>, EI extends SimpleId<?>> extends Endring<I, EI> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("UnusedDeclaration") // Hibernate og AbstractEndringManager
    public SimpleEndring() {
        super();
    }

    public SimpleEndring(Long value) {
        setId(new SimpleEndringId<SimpleEndring>(value));
    }

    @Override
    public I getId() {
        EndringId<?> id = (EndringId<?>) super.getId();
        if (id == null) {
            return null;
        }
        if (id instanceof SimpleEndringId) {
            return (I) id;
        }
        return (I) new SimpleEndringId<>(id.getValue(), id.getSnapshotVersion());
    }

    @Override
    public EI getEndretBubbleId() {
        BubbleId<?> id = super.getEndretBubbleId();
        if (id == null) {
            return null;
        }
        if (id instanceof SimpleId) {
            return (EI) id;
        }
        return (EI) new SimpleId<>((Long) id.getValue(), id.getSnapshotVersion());
    }

}
