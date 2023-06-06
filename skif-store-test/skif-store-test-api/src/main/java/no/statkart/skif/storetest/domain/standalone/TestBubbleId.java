package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestBubbleId<T extends TestBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public TestBubbleId(Long idValue) {
        super(idValue);
    }

    public TestBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
