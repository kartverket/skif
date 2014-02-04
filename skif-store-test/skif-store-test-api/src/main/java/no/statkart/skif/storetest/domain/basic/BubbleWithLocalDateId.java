package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * Id for {@link BubbleWithLocalDate}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class BubbleWithLocalDateId<T extends BubbleWithLocalDate> extends AbstractStoreTestBubbleId<T> {
    public BubbleWithLocalDateId(Long value) {
        super(value);
    }

    public BubbleWithLocalDateId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
