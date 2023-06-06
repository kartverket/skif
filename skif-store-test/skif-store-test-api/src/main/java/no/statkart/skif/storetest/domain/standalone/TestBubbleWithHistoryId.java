package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class TestBubbleWithHistoryId<T extends TestBubbleWithHistory> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public TestBubbleWithHistoryId(Long idValue) {
        super(idValue);
    }

    public TestBubbleWithHistoryId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
