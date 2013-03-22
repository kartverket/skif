package no.statkart.skif.store.endringslogg;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link AbstractEndring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class AbstractEndringId<T extends AbstractEndring> extends AbstractBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public AbstractEndringId() {
    }

    public AbstractEndringId(Long value) {
        super(value);
    }

    public AbstractEndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
