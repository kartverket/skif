package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

public class ParentBubbleEmptyColOptimizerId<T extends ParentBubbleEmptyColOptimizer> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ParentBubbleEmptyColOptimizerId() {
        super();
    }

    public ParentBubbleEmptyColOptimizerId(int idValue) {
        super(new Long(idValue));
    }

    public ParentBubbleEmptyColOptimizerId(Long idValue) {
        super(idValue);
    }

    public ParentBubbleEmptyColOptimizerId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public BubbleId<? super T> asBase() {
        return new ParentBubbleEmptyColOptimizerId<>(getValue(), getSnapshotVersion());
    }
}
