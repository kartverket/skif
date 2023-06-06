package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

public class ChildBubbleEmptyColOptimizerId<T extends ChildBubbleEmptyColOptimizer> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ChildBubbleEmptyColOptimizerId(Long idValue) {
        super(idValue);
    }

    public ChildBubbleEmptyColOptimizerId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
