package no.statkart.skif.storetest.domain.component.composite;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.CompositeComponent;
import no.statkart.skif.store.CompositeComponentWithCollections;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public class Level2CompositeComponent implements CompositeComponent<Level1CompositeComponent>, CompositeComponentWithCollections {
    private Level1CompositeComponent owner;
    private String text;
    private BeloepValueObject belop;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();
    private EntityInCompositeComponent entity;
    private Set<EntityInCompositeComponent> entitySet = Sets.newHashSet();

    @Override
    public Level1CompositeComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Level1CompositeComponent owner) {
        this.owner = Components.checkSetOwner(
                this,
                this.owner,
                owner,
                new Function<Level1CompositeComponent, Level2CompositeComponent>() {
                    public Level2CompositeComponent apply(Level1CompositeComponent owner) {
                        return owner.getLevel2Component();
                    }
                }
        );
    }

    @Override
    public boolean isNullComponent() {
        return this.text==null
                && this.belop==null
                && this.entity==null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BeloepValueObject getBelop() {
        return belop;
    }

    public void setBelop(BeloepValueObject belop) {
        this.belop = belop;
    }

    public Set<BeloepValueObject> getBeloepSet() {
        return beloepSet;
    }

    public void setBeloepSet(Set<BeloepValueObject> beloepSet) {
        this.beloepSet = beloepSet;
    }

    public EntityInCompositeComponent getEntity() {
        return entity;
    }

    public void setEntity(EntityInCompositeComponent entity) {
        this.entity = entity;
    }

    public Set<EntityInCompositeComponent> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<EntityInCompositeComponent> entitySet) {
        this.entitySet = entitySet;
    }
}
