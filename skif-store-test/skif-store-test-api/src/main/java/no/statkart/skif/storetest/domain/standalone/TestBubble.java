package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Boble som ikke har historikk.
 * <p>
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text = "";

    public TestBubble() {
    }

    public TestBubble(TestBubbleId<?> id) {
        this(id, null);
    }

    public TestBubble(TestBubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId((TestBubbleId<?>)id);
    }

    @Override
    public TestBubbleId<?> getId() {
        return (TestBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}