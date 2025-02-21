package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.AbstractCompositeComponent;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.CompositeComponentWithCollections;

import java.util.Set;

/**
 * En composite component som har {@code Level1CompositeComponentWithEntity} som owner. Inneholder
 * referanse til en entity og et sett av entities. Disse har også {@code BubbleWithEntityInCompositeComponent} som owner.
 *
 * @author Henrik Fredholm
 * @since 2.4
 *
 */
public class Level2CompositeComponentWithEntity extends AbstractCompositeComponent<BubbleWithEntityInCompositeComponent, Level1CompositeComponentWithEntity> implements CompositeComponentWithCollections {
    private String text;
    private Level2EntityInCompositeComponent entity;
    private Set<Level2SetEntityInCompositeComponent> entitySet = Components.newSet(this);

    @SuppressWarnings("UnusedDeclaration") //Hibernate
    public Level2CompositeComponentWithEntity() {
    }

    public Level2CompositeComponentWithEntity(String text) {
        this.text = text;
    }

    @Override
    public void onSetCompositeRootOwner() {
        onSetCompositeRootOwner(entity);
        onSetCompositeRootOwner(entitySet);
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
        this.entity = Components.checkSetComponent(this.getCompositeRootOwner(), this.entity, entity);
    }

    public Set<Level2SetEntityInCompositeComponent> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<Level2SetEntityInCompositeComponent> entitySet) {
        Components.setFrom(this.entitySet, entitySet);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private Set<Level2SetEntityInCompositeComponent> getEntitySetHibernate() {
        return Components.getDelegate(entitySet);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setEntitySetHibernate(Set<Level2SetEntityInCompositeComponent> entitySet) {
        Components.setDelegate(this.entitySet, entitySet);
    }
}
