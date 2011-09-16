package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestBubble extends StoreTestBubble {
    private String text = "";

    public TestBubble() {
    }

    public TestBubble(TestBubbleId<?> id) {
        super(id);
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