package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.SnapshotVersion;

public class ParentBubbleEmptyColOptimizerSub1Id<T extends ParentBubbleEmptyColOptimizerSub1> extends ParentBubbleEmptyColOptimizerId<T> {

    public ParentBubbleEmptyColOptimizerSub1Id(Long idValue) {
        super(idValue);
    }

    public ParentBubbleEmptyColOptimizerSub1Id(Long value, SnapshotVersion version) {
        super(value, version);
    }

}
