package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2AAWithEntityComponent extends AbstractRelationTestBubble implements InverseRelationParticipation{
    private static final long serialVersionUID = 1L;
    private X2EntityComponentOne entityComponentOne;

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
        collector.collectInverseRelationValues(entityComponentOne);
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

}