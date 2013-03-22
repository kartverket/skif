package no.statkart.skif.storetest2.domain.eierskap;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.AbstractStoreTest2BubbleId;

/**
 * Id from {@link Eier}.
 *
 * @author Tor Egil R. Strand
 */
public class EierId<T extends Eier> extends AbstractStoreTest2BubbleId<T> {
    private static final long serialVersionUID = 1L;

    public EierId(Long value) {
        super(value);
    }

    public EierId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
