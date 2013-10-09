package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.CompositeBubbleComponent;
import no.statkart.skif.store.CompositeComponentWithCollections;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * En composite component som har {@code BubbleWithEntityInCompositeComponent} som owner og som inneholder
 * referanse til en entity og et sett av entities. Disse har også {@code BubbleWithEntityInCompositeComponent} som owner.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level1CompositeComponentWithEntity implements CompositeBubbleComponent<BubbleWithEntityInCompositeComponent>, CompositeComponentWithCollections {
    private BubbleWithEntityInCompositeComponent owner;
    private String text;
    private Level1EntityInCompositeComponent entity;
    private final Set<Level1SetEntityInCompositeComponent> entitySet = Components.newSet(this);

    private Level2CompositeComponentWithEntity level2Component;

    public Level1CompositeComponentWithEntity() {
    }

    public Level1CompositeComponentWithEntity(String text) {
        this.text = text;
    }

    @Override
    public BubbleWithEntityInCompositeComponent getOwner() {
        return owner;
    }

    @Override
    public BubbleWithEntityInCompositeComponent getCompositeRootOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithEntityInCompositeComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    @Nullable
    public Level2CompositeComponentWithEntity getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable Level2CompositeComponentWithEntity level2Component) {
        this.level2Component = Components.checkSetComponent(this, this.level2Component, level2Component);
    }

    @Override
    public boolean isNullComponent() {
        return this.text == null
                && this.entity == null
                && this.getEntitySet().isEmpty()
                && Components.isNullComponent(level2Component);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Level1EntityInCompositeComponent getEntity() {
        return entity;
    }

    public void setEntity(Level1EntityInCompositeComponent entity) {
        this.entity = Components.checkSetComponent(this.getCompositeRootOwner(), this.entity, entity);
    }

    public Set<Level1SetEntityInCompositeComponent> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<Level1SetEntityInCompositeComponent> entitySet) {
        Components.setFrom(this.entitySet, entitySet);
    }

    public Set<Level1SetEntityInCompositeComponent> getEntitySetHibernate() {
        return Components.getDelegate(entitySet);
    }

    public void setEntitySetHibernate(Set<Level1SetEntityInCompositeComponent> entitySet) {
        Components.setDelegate(this.entitySet, entitySet);
    }
}
