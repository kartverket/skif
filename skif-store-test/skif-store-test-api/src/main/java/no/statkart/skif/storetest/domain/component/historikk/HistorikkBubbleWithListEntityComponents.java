package no.statkart.skif.storetest.domain.component.historikk;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse med en liste av value components.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class HistorikkBubbleWithListEntityComponents extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    private List<HistorikkListEntityComponent> entityComponents = new ArrayList<HistorikkListEntityComponent>();

    @Override
    public HistorikkBubbleWithListEntityComponentsId<?> getId() {
        return (HistorikkBubbleWithListEntityComponentsId<?>) super.getId();
    }

    public List<HistorikkListEntityComponent> getEntityComponents() {
        return entityComponents;
    }

    public void setEntityComponents(List<HistorikkListEntityComponent> entityComponents) {
        this.entityComponents = entityComponents;
    }
}
