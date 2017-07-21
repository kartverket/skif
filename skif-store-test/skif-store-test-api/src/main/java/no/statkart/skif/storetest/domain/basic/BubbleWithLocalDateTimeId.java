package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * Id for {@link BubbleWithLocalDateTime}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class BubbleWithLocalDateTimeId<T extends BubbleWithLocalDateTime> extends AbstractStoreTestBubbleId<T> {
    @SuppressWarnings("unused")
    public BubbleWithLocalDateTimeId(Long value) {
        super(value);
    }

    @SuppressWarnings("unused")
    public BubbleWithLocalDateTimeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
