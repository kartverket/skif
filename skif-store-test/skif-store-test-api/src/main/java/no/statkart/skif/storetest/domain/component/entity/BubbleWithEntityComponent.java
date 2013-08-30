package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Boble som har entity komponenter nestede nivåer
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithEntityComponent extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    /* Angir logisk nummer på boblen innen for et testset*/
    private int nr;
    /* En tekst som beskriver boblen */
    private String text;
    private Level1EntityComponent level1Component;
    private Set<SetAaEntityComponent> aaComponents = Sets.newHashSet();

    public BubbleWithEntityComponent() {
    }

    public BubbleWithEntityComponent(BubbleWithCompositeComponentId<?> id) {
        super(id);
    }

    public BubbleWithEntityComponent(BubbleWithCompositeComponentId id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public BubbleWithEntityComponentId<?> getId() {
        return (BubbleWithEntityComponentId<?>) super.getId();
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
    public Level1EntityComponent getLevel1Component() {
        return level1Component;
    }

    public void setLevel1Component(Level1EntityComponent level1Component) {
        this.level1Component = Components.checkSetComponentWithOwner(this.level1Component, level1Component);
        Components.setOwner(this.level1Component, this);
    }

    public Set<SetAaEntityComponent> getAaComponents() {
        return aaComponents;
    }

    public void setAaComponents(Set<SetAaEntityComponent> aaComponents) {
        this.aaComponents = aaComponents;
    }
}
