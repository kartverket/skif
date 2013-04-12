package no.statkart.skif.storetest2.domain.list;

import no.statkart.skif.storetest2.domain.AbstractStoreTest2Bubble;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse med en liste av value components.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class ListOfEntityComponents extends AbstractStoreTest2Bubble {
    private static final long serialVersionUID = 1L;

    private List<ListEntityComponent> entityComponents = new ArrayList<ListEntityComponent>();

    @Override
    public ListOfEntityComponentsId<?> getId() {
        return (ListOfEntityComponentsId<?>) super.getId();
    }

    public List<ListEntityComponent> getEntityComponents() {
        return entityComponents;
    }

    public void setEntityComponents(List<ListEntityComponent> entityComponents) {
        this.entityComponents = entityComponents;
    }
}
