package no.statkart.skif.storetest2.domain.entitycomponent;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.AbstractStoreTest2BubbleId;

/**
 * Id for {@link BubbleWithEntityComponents}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class BubbleWithEntityComponentsId<T extends BubbleWithEntityComponents> extends AbstractStoreTest2BubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithEntityComponentsId(Long value) {
        super(value);
    }

    public BubbleWithEntityComponentsId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
