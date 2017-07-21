package no.statkart.skif.storetest.domain.component.historikk;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

import java.util.HashSet;
import java.util.Set;

/**
 * Tester måter entitycomponents kan brukes på.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class HistorikkBubbleWithEntityComponents extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    private HistorikkEntityComponent mainEntityComponent;
    private Set<HistorikkEntityComponent> secondaryEntityComponents = new HashSet<>();

    @Override
    public HistorikkBubbleWithEntityComponentsId<?> getId() {
        return (HistorikkBubbleWithEntityComponentsId<?>) super.getId();
    }

    public HistorikkEntityComponent getMainEntityComponent() {
        return mainEntityComponent;
    }

    public void setMainEntityComponent(HistorikkEntityComponent mainEntityComponent) {
        this.mainEntityComponent = mainEntityComponent;
    }

    public Set<HistorikkEntityComponent> getSecondaryEntityComponents() {
        return secondaryEntityComponents;
    }

    public void setSecondaryEntityComponents(Set<HistorikkEntityComponent> secondaryEntityComponents) {
        this.secondaryEntityComponents = secondaryEntityComponents;
    }
}
