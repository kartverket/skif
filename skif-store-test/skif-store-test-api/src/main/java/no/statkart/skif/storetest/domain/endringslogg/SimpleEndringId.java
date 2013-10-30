package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link SimpleEndring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SimpleEndringId<T extends SimpleEndring> extends EndringId<T> {
    private static final long serialVersionUID = 1L;

    public SimpleEndringId(Long value) {
        super(value);
    }

    public SimpleEndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
