package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Boble som kan inngå ParentBubbleEmptyColOptimizer sine collections.
 * <p>
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 */
public class ChildBubbleEmptyColOptimizer extends AbstractBubbleObject implements StoreTestBubble {
    private String text;

    public ChildBubbleEmptyColOptimizer() {
    }

    public ChildBubbleEmptyColOptimizer(BubbleId<?> id) {
        super(id);
    }

    public ChildBubbleEmptyColOptimizer(BubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId(id);
    }

    @Override
    public ChildBubbleEmptyColOptimizerId<?> getId() {
        return (ChildBubbleEmptyColOptimizerId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ChildBubbleInColOptimizer{" +
                ", text='" + text + '\'' +
                '}';
    }
}
