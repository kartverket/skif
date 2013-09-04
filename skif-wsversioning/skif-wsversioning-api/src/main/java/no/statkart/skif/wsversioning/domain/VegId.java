package no.statkart.skif.wsversioning.domain;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link Veg}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class VegId<T extends Veg> extends AbstractWSVersioningBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public VegId() {
    }

    public VegId(Long value) {
        super(value);
    }

    public VegId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
