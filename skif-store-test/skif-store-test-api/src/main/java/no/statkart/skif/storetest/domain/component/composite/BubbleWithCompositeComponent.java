package no.statkart.skif.storetest.domain.component.composite;

import no.statkart.skif.store.Components;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import javax.annotation.Nullable;

/**
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithCompositeComponent extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private Level1CompositeComponent level1Component;

    public BubbleWithCompositeComponent() {
    }

    public BubbleWithCompositeComponent(BubbleWithCompositeComponentId<?> id) {
        super(id);
    }

    public BubbleWithCompositeComponent(BubbleWithCompositeComponentId id, String text) {
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

    @Nullable
    public Level1CompositeComponent getLevel1Component() {
        return level1Component;
    }

    public void setLevel1Component(Level1CompositeComponent level1Component) {
        this.level1Component = Components.checkSetComponent(this, this.level1Component, level1Component);
    }
}
