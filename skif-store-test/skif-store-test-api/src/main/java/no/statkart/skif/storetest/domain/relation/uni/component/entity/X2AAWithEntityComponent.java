package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.storetest.domain.component.entity.SetAaEntityComponent;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2AAWithEntityComponent extends AbstractRelationTestBubble implements InverseRelationParticipation{
    private static final long serialVersionUID = 1L;
    private X2EntityComponentOne entityComponentOne;
    private final Set<X2SetEntityComponent> aaSetEntityComponents = Components.newSet(this);

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
        collector.collectInverseRelationValues(entityComponentOne);
        collector.collectInverseRelationValues(aaSetEntityComponents);
    }

    @Override
    public X2AAWithEntityComponentId<?> getId() {
        return (X2AAWithEntityComponentId<?>) super.getId();
    }

    public X2EntityComponentOne getEntityComponentOne() {
        return entityComponentOne;
    }

    public void setEntityComponentOne(X2EntityComponentOne entityComponentOne) {
        this.entityComponentOne = Components.checkSetComponent(this, this.entityComponentOne, entityComponentOne);
    }


    public Set<X2SetEntityComponent> getAaSetEntityComponents() {
        return aaSetEntityComponents;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private Set<X2SetEntityComponent> getAaSetEntityComponentsSet() {
        return Components.getDelegate(aaSetEntityComponents);

    }

    public void setAaSetEntityComponents(Set<X2SetEntityComponent> aaSetEntityComponents) {
        Components.setFrom(this.aaSetEntityComponents, aaSetEntityComponents);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setAaSetEntityComponentsSet(Set<X2SetEntityComponent> aaSetEntityComponents) {
        Components.setDelegate(this.aaSetEntityComponents, aaSetEntityComponents);
    }

}