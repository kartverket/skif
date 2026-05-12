package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SelfBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text = "";
    private SelfBubbleId<?> refId;

    public SelfBubble() {
    }

    public SelfBubble(SelfBubbleId<?> id) {
        this(id, null);
    }

    public SelfBubble(SelfBubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public SelfBubbleId<?> getId() {
        return (SelfBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SelfBubbleId<?> getRefId() {
        return refId;
    }

    public void setRefId(SelfBubbleId<?> refId) {
        this.refId = refId;
    }
}
