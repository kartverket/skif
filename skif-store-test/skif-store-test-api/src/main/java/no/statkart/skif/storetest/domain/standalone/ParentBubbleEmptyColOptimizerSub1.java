package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Bubbles;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 */
public class ParentBubbleEmptyColOptimizerSub1 extends ParentBubbleEmptyColOptimizer {
    private Set<ChildBubbleEmptyColOptimizerId> children4Ids = new HashSet<>(); // Bruker bitt 4, se mapping fil

    public ParentBubbleEmptyColOptimizerSub1() {
    }

    public ParentBubbleEmptyColOptimizerSub1(BubbleId<?> id) {
        super(id);
    }

    @Override
    public ParentBubbleEmptyColOptimizerSub1Id<?> getId() {
        return (ParentBubbleEmptyColOptimizerSub1Id<?>) super.getId();
    }

    public Set<ChildBubbleEmptyColOptimizerId> getChildren4Ids() {
        return children4Ids;
    }

    public ParentBubbleEmptyColOptimizerSub1 setChildren1Ids(Set<ChildBubbleEmptyColOptimizerId> children4Ids) {
        Bubbles.setFrom(this.children4Ids, children4Ids);
        return this;
    }
}
