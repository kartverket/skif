package no.statkart.skif.storetest2.domain.subtype;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest2.domain.StoreTest2Bubble;

/**
 * Testbobleklasse for endring av subtype og andre subtyperelaterte ting.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class SubTypedBubble extends AbstractBubbleObject implements StoreTest2Bubble {
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
