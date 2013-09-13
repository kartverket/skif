package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.CompositeBubbleComponent;
import no.statkart.skif.store.CompositeComponentWithCollections;
import no.statkart.skif.store.OwnerCheck;

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
    private Set<Level1SetEntityInCompositeComponent> entitySet = Sets.newHashSet();

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
    public void setOwner(BubbleWithEntityInCompositeComponent owner) {
       this.owner = Components.checkSetOwner(
               this,
               this.owner,
               owner,
               new OwnerCheck<BubbleWithEntityInCompositeComponent, Level1CompositeComponentWithEntity>() {
                   public boolean apply(BubbleWithEntityInCompositeComponent owner, Level1CompositeComponentWithEntity child) {
                       return owner.getLevel1Component()==child;
                   }
               }
       );
    }

    @Nullable
    public Level2CompositeComponentWithEntity getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable Level2CompositeComponentWithEntity level2Component) {
        this.level2Component = Components.checkSetComponentWithOwner(this.level2Component, level2Component);
        Components.setOwner(this.level2Component, this);
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
        this.entity = Components.checkSetComponentWithOwner(this.entity, entity);
        Components.setOwner(this.entity, this.getOwner());
    }

    public Set<Level1SetEntityInCompositeComponent> getEntitySet() {
        return Components.get(this.getOwner(), entitySet);
    }

    public void setEntitySet(Set<Level1SetEntityInCompositeComponent> entitySet) {
        Components.setFrom(this.getOwner(), this.entitySet, entitySet);
    }

}
