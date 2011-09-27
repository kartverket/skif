package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store2.AbstractBubbleObject2;
import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.TestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestBubble2 extends AbstractBubbleObject2 implements StoreTestBubble2 {
    private String text = "";

    public TestBubble2() {
    }

    public TestBubble2(TestBubbleId2<?> id) {
        super(id);
    }

    @Override
    public void setId(BubbleId2<?> id) {
        super.setId((TestBubbleId2<?>)id);
    }

    @Override
    public TestBubbleId2<?> getId() {
        return (TestBubbleId2<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}