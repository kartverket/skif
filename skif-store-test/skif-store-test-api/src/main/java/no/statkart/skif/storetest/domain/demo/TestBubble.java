package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
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