package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityComponentWithOwnerReference;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;

/**
 * En EntityComponent som inngår i et sett i boblen X2AAWithEntityComponent og som har en referanse til X2BBOne.
 */
public class X2SetEntityComponent implements EntityComponentWithOwnerReference<X2AAWithEntityComponent>, InverseRelationParticipation {
    private Long id;
    private X2AAWithEntityComponent owner;
    private String text;
    private X2BBOneId<?> role1BBOneId;

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
        collector.put(X2AAWithEntityComponentFinderService.Role.role1BBs, role1BBOneId);
    }

    @Override
    public X2AAWithEntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(X2AAWithEntityComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    @Override
    public Long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public X2BBOneId<?> getRole1BBOneId() {
        return role1BBOneId;
    }

    public X2BBOne getRole1BBOne() {
        return Components.getOwningBubbleNullSafe(this).store().get(role1BBOneId);
    }


    public void setRole1BBOneId(X2BBOneId role1BBOneId) {
        this.role1BBOneId = Components.onChangeRelation(this, X2AAWithEntityComponentFinderService.Role.role1BBs, this.role1BBOneId, role1BBOneId);
    }
}
