package no.statkart.skif.storetest.history;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestHistoricBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text = "";

    public TestHistoricBubble() {
    }

    public TestHistoricBubble(TestHistoricBubbleId<?> id) {
        super(id);
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId((TestHistoricBubbleId<?>)id);
    }

    @Override
    public TestHistoricBubbleId<?> getId() {
        return (TestHistoricBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}