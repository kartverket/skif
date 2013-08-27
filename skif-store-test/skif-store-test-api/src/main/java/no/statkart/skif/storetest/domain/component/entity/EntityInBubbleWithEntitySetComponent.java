package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.EntityBubbleComponent;

/**
 * Entity som inngår i et set som ligger i BubbleWithEntityComponent
 * @author Henrik Fredholm
 */
public class EntityInBubbleWithEntitySetComponent implements EntityBubbleComponent<BubbleWithEntityComponent> {
    private int ident;
    private String text;

    @Override
    public Long getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EntityInBubbleWithEntitySetComponent() {
    }

    public EntityInBubbleWithEntitySetComponent(String text) {
        this.text = text;
    }

    @Override
    public BubbleWithEntityComponent getOwner() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOwner(BubbleWithEntityComponent owner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
