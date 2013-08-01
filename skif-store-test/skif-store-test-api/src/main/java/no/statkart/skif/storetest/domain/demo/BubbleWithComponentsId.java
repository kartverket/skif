package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class BubbleWithComponentsId<T extends BubbleWithComponents> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithComponentsId(Long value) {
        super(value);
    }

    public BubbleWithComponentsId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
