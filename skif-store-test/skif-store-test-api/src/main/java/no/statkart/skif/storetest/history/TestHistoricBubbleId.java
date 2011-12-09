package no.statkart.skif.storetest.history;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestHistoricBubbleId<T extends TestHistoricBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    public TestHistoricBubbleId<T> resolveInstance() {
        return this;
    }

    public TestHistoricBubbleId() {
        super();
    }

    public TestHistoricBubbleId(int idValue) {
        super(new Long(idValue));
    }

    public TestHistoricBubbleId(Object idValue) {
        super(idValue);
    }

    public TestHistoricBubbleId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

}
