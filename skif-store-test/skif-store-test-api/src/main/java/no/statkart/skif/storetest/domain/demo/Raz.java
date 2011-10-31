package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class Raz extends AbstractBubbleObject implements StoreTestBubble {

    private String text;
    private RazComponent razComponent;

    @Override
    public RazId<?> getId() {
        return (RazId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RazComponent getRazComponent() {
        return razComponent;
    }

    public void setRazComponent(RazComponent razComponent) {
        this.razComponent = razComponent;
    }
}
