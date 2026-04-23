package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import jakarta.annotation.Nullable;

/**
 * Boble med composite componenter i nestede nivåer og hvor hver composite component inneholder en entity og et sett
 * av entities
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class BubbleWithEntityInCompositeComponent extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    /* Angir logisk nummer på boblen innen for et testset*/
    private int nr;
    /* En tekst som beskriver boblen */
    private String text;
    private Level1CompositeComponentWithEntity level1Component;

    public BubbleWithEntityInCompositeComponent() {
    }

    public BubbleWithEntityInCompositeComponent(BubbleWithEntityInCompositeComponentId<?> id) {
        super(id);
    }

    public BubbleWithEntityInCompositeComponent(BubbleWithEntityInCompositeComponentId id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public BubbleWithEntityInCompositeComponentId<?> getId() {
        return (BubbleWithEntityInCompositeComponentId<?>) super.getId();
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
    public Level1CompositeComponentWithEntity getLevel1Component() {
        return level1Component;
    }

    public void setLevel1Component(Level1CompositeComponentWithEntity level1Component) {
        this.level1Component = Components.checkSetComponent(this, this.level1Component, level1Component);
    }
}
