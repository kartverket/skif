package no.statkart.skif.storetest2.domain.endringslogg;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link EiendomEndring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EiendomEndringId<T extends EiendomEndring> extends EndringId<T> {
    public EiendomEndringId(Long value) {
        super(value);
    }

    public EiendomEndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
