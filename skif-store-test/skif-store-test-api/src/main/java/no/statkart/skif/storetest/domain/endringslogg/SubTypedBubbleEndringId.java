package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link SubTypedBubbleEndring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SubTypedBubbleEndringId<T extends SubTypedBubbleEndring> extends EndringId<T> {
    private static final long serialVersionUID = 1L;

    public SubTypedBubbleEndringId(Long value) {
        super(value);
    }

    public SubTypedBubbleEndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
