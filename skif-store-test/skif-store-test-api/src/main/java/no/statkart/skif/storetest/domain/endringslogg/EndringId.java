package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * Id for {@link Endring}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringId<T extends Endring> extends AbstractEndringId<T> implements StoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public EndringId(Long value) {
        super(value);
    }

    public EndringId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
