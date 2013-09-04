package no.statkart.skif.wsversioning.domain;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Baseklasse for alle vanlige bobleid-er i WSVersioning-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public abstract class AbstractWSVersioningBubbleId<T extends WSVersioningBubbleObject> extends AbstractBubbleId<T> implements WSVersioningBubbleId<T> {
    private static final long serialVersionUID = 1L;

    protected AbstractWSVersioningBubbleId() {
    }

    protected AbstractWSVersioningBubbleId(Long value) {
        super(value);
    }

    protected AbstractWSVersioningBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
