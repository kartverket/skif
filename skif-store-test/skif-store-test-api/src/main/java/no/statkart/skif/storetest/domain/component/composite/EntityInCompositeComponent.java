package no.statkart.skif.storetest.domain.component.composite;

import no.statkart.skif.store.EntityBubbleComponent;

/**
 * @author Henrik Fredholm
 */
public class EntityInCompositeComponent implements EntityBubbleComponent<BubbleWithCompositeComponent> {
    private String text;

    public EntityInCompositeComponent() {
    }

    @Override
    public Long getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EntityInCompositeComponent(String text) {
        this.text = text;
    }

    @Override
    public BubbleWithCompositeComponent getOwner() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOwner(BubbleWithCompositeComponent owner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
