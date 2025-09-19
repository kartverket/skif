package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

public class BubbleWithSubtypedEntityComponent extends AbstractStoreTestBubble {
    private SubtypedEntityComponent subtypedEntityComponent;

    @Override
    public BubbleWithSubtypedEntityComponentId<?> getId() {return (BubbleWithSubtypedEntityComponentId<?>) super.getId();}

    public SubtypedEntityComponent getSubtypedEntityComponent() {return subtypedEntityComponent;}

    public void setSubtypedEntityComponent(SubtypedEntityComponent subtypedEntityComponent) {
        this.subtypedEntityComponent = Components.checkSetComponent(this, this.subtypedEntityComponent, subtypedEntityComponent);
    }
}
