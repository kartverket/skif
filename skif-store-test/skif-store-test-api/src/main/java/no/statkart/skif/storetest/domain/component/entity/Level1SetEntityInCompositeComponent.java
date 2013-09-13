package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityBubbleComponent;
import no.statkart.skif.store.OwnerCheck;

/**
 * En entity som inngår i en composite component, som ligger i et sett og som har
 * {@code BubbleWithEntityInCompositeComponent} som owner.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level1SetEntityInCompositeComponent extends AbstractEntityBubbleComponentWithOwner<BubbleWithEntityInCompositeComponent> {
    private Long id;
    private String text;
    private BubbleWithEntityInCompositeComponent owner;

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public Level1SetEntityInCompositeComponent() {
    }

    @Override
    public Long getId() {
        return id;
    }


    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    public Level1SetEntityInCompositeComponent(String text) {
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
                new OwnerCheck<BubbleWithEntityInCompositeComponent, Level1SetEntityInCompositeComponent>() {
                    public boolean apply(BubbleWithEntityInCompositeComponent owner, Level1SetEntityInCompositeComponent child) {
                        return owner.getLevel1Component().getEntitySet().contains(child);
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
