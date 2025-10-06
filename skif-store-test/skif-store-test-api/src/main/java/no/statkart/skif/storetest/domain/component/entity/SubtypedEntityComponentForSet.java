package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;

public abstract class SubtypedEntityComponentForSet extends AbstractEntityBubbleComponentWithOwner<BubbleWithSubtypedEntityComponentSet> implements InverseRelationParticipation {
    private Long id;
    private BubbleWithSubtypedEntityComponentSet owner;
    private long nr;

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {return id;}
    @Override
    public BubbleWithSubtypedEntityComponentSet getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithSubtypedEntityComponentSet owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
    }

    public long getNr() {return nr;}
    public void setNr(long nr) {this.nr = nr;}
}
