package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityBubbleComponent;
import no.statkart.skif.store.OwnerCheck;

/**
 * En entity som inngår i en composite component og som har som har {@code BubbleWithEntityInCompositeComponent} som owner.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level2EntityInCompositeComponent implements EntityBubbleComponent<BubbleWithEntityInCompositeComponent> {
    private Long id;
    private String text;
    private BubbleWithEntityInCompositeComponent owner;

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
        this.owner = Components.checkSetOwner(
                this,
                this.owner,
                owner,
                new OwnerCheck<BubbleWithEntityInCompositeComponent, Level2EntityInCompositeComponent>() {
                    public boolean apply(BubbleWithEntityInCompositeComponent owner, Level2EntityInCompositeComponent child) {
                        return owner.getLevel1Component().getLevel2Component().getEntity() == child;
                    }
                }
        );
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
