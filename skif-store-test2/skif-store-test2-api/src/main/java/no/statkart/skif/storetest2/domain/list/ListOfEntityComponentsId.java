package no.statkart.skif.storetest2.domain.list;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.AbstractStoreTest2BubbleId;

/**
 * Id for {@link ListOfEntityComponents}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class ListOfEntityComponentsId<T extends ListOfEntityComponents> extends AbstractStoreTest2BubbleId<T> {
    private static final long serialVersionUID = 1L;

    public ListOfEntityComponentsId(Long value) {
        super(value);
    }

    public ListOfEntityComponentsId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
