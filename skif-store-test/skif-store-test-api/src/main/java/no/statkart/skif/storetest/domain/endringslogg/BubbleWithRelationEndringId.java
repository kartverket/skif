package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link BubbleWithRelationEndring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class BubbleWithRelationEndringId<T extends BubbleWithRelationEndring> extends EndringId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithRelationEndringId(Long value) {
        super(value);
    }

    @SuppressWarnings("unused")
    public BubbleWithRelationEndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
