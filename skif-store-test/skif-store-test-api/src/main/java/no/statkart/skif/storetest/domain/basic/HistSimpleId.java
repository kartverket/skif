package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistoryId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */

public class HistSimpleId<T extends HistSimple> extends AbstractStoreTestBubbleWithHistoryId<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public HistSimpleId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public HistSimpleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
