package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
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
    public void setId(BubbleId<?> id) {
        super.setId((SelfBubbleId<?>)id);
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
