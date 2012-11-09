package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Testbobleklasse for endring av subtype og andre subtyperelaterte ting.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class SubTypedBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text;

    @Override
    public SubTypedBubbleId<?> getId() {
        return (SubTypedBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
