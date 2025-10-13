package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import java.util.Collection;
import java.util.Set;

public class BubbleWithSubtypedEntityComponentSet extends AbstractStoreTestBubble {
    private final Set<SubtypedEntityComponentForSet> subtypedEntityComponentSet = Components.newSet(this);

    @Override
    public BubbleWithSubtypedEntityComponentSetId<?> getId() {
        return (BubbleWithSubtypedEntityComponentSetId<?>) super.getId();
    }

    public Set<SubtypedEntityComponentForSet> getSubtypedEntityComponentSet() {
        return subtypedEntityComponentSet;
    }

    private Set<SubtypedEntityComponentForSet> getSubtypedEntityComponentSetSet() {
        return Components.getDelegate(subtypedEntityComponentSet);

    }

    public void setSubtypedEntityComponentSet(Collection<SubtypedEntityComponentForSet> subtypedEntityComponentCollection) {
        Components.setFrom(this.subtypedEntityComponentSet, subtypedEntityComponentCollection);
    }


    private void setSubtypedEntityComponentSetSet(Set<SubtypedEntityComponentForSet> entityComponents) {
        Components.setDelegate(this.subtypedEntityComponentSet, entityComponents);
    }

}
