package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.*;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * En composite component som har {@code BubbleWithEntityInCompositeComponent} som owner og som inneholder
 * referanse til en entity og et sett av entities. Disse har også {@code BubbleWithEntityInCompositeComponent} som owner.
 *
 * @author Henrik Fredholm
 * @since 2.4
 *
 */
public class Level2CompositeComponentWithEntity implements CompositeComponent<Level1CompositeComponentWithEntity>, CompositeComponentWithCollections {
    private Level1CompositeComponentWithEntity owner;
    private String text;
    private Level2EntityInCompositeComponent entity;
    private Set<Level2SetEntityInCompositeComponent> entitySet = Sets.newHashSet();

    @SuppressWarnings("UnusedDeclaration") //Hibernate
    public Level2CompositeComponentWithEntity() {
    }

    public Level2CompositeComponentWithEntity(String text) {
        this.text = text;
    }

    @Override
    public Level1CompositeComponentWithEntity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Level1CompositeComponentWithEntity owner) {
       this.owner = Components.checkSetOwner(
               this,
               this.owner,
               owner,
               new OwnerCheck<Level1CompositeComponentWithEntity, Level2CompositeComponentWithEntity>() {
                   public boolean apply(Level1CompositeComponentWithEntity owner, Level2CompositeComponentWithEntity child) {
                       return owner.getLevel2Component()==child;
                   }
               }
       );
    }

    @Override
    public boolean isNullComponent() {
        return this.text == null
                && this.entity == null
                && this.getEntitySet().isEmpty();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Level2EntityInCompositeComponent getEntity() {
        return entity;
    }

    public void setEntity(Level2EntityInCompositeComponent entity) {
        this.entity = Components.checkSetComponentWithOwner(this.entity, entity);
        Components.setOwner(this.entity, this.getOwner().getOwner());
    }

    public Set<Level2SetEntityInCompositeComponent> getEntitySet() {
        return Components.get(this.getOwner().getOwner(), entitySet);
    }

    public void setEntitySet(Set<Level2SetEntityInCompositeComponent> entitySet) {
        Components.setFrom(this.getOwner().getOwner(), this.entitySet, entitySet);
    }

}
