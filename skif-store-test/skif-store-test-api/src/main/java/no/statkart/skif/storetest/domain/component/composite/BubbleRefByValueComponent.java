package no.statkart.skif.storetest.domain.component.composite;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

/**
 * Boble uten historikk og som ikke har egne relasjoner til andre objekter
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleRefByValueComponent extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private Level1CompositeComponent level1Component;

    public BubbleRefByValueComponent() {
    }

    public BubbleRefByValueComponent(BubbleWithCompositeComponentId<?> id) {
        super(id);
    }

    public BubbleRefByValueComponent(BubbleWithCompositeComponentId id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public BubbleWithCompositeComponentId<?> getId() {
        return (BubbleWithCompositeComponentId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
