package no.statkart.skif.storetest2.domain;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link AbstractStoreTest2BubbleWithHistory}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public abstract class AbstractStoreTest2BubbleWithHistoryId<T extends AbstractStoreTest2BubbleWithHistory> extends AbstractStoreTest2BubbleId<T> {
    public AbstractStoreTest2BubbleWithHistoryId(Long value) {
        super(value);
    }

    public AbstractStoreTest2BubbleWithHistoryId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
