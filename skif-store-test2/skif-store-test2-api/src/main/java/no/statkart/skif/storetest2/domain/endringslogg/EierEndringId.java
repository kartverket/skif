package no.statkart.skif.storetest2.domain.endringslogg;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link EierEndring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EierEndringId<T extends EierEndring> extends EndringId<T> {
    private static final long serialVersionUID = 1L;

    public EierEndringId(Long value) {
        super(value);
    }

    public EierEndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
