package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;

public abstract class SubtypedEntityComponent extends AbstractEntityBubbleComponentWithOwner<BubbleWithSubtypedEntityComponent> implements InverseRelationParticipation {
    private Long id;
    private BubbleWithSubtypedEntityComponent owner;
    private Long nr;

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {return id;}
    @Override
    public BubbleWithSubtypedEntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithSubtypedEntityComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
    }

    public Long getNr() {return nr;}
    public void setNr(Long nr) {this.nr = nr;}
}
