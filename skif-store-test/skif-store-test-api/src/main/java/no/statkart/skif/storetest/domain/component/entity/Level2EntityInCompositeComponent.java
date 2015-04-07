package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityBubbleComponent;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Map;

/**
 * En entity som inngår i en composite component og som har som har {@code BubbleWithEntityInCompositeComponent} som owner.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level2EntityInCompositeComponent implements EntityBubbleComponent<BubbleWithEntityInCompositeComponent>, InverseRelationParticipation {
    private BubbleWithEntityInCompositeComponent owner;
    private Long id;
    private String text;

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public Level2EntityInCompositeComponent() {
    }

    @Override
    public Long getId() {
        return id;
    }


    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    public Level2EntityInCompositeComponent(String text) {
        this.text = text;
    }

    @Override
    public BubbleWithEntityInCompositeComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithEntityInCompositeComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
    }
}
