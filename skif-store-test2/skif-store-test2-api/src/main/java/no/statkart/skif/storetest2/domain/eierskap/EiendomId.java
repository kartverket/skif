package no.statkart.skif.storetest2.domain.eierskap;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.AbstractStoreTest2BubbleId;

/**
 * Id from {@link Eiendom}.
 *
 * @author Tor Egil R. Strand
 */
public class EiendomId<T extends Eiendom> extends AbstractStoreTest2BubbleId<T> {
    private static final long serialVersionUID = 1L;

    public EiendomId(Long value) {
        super(value);
    }

    public EiendomId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
