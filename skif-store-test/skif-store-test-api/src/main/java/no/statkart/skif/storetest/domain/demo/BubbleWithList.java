package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Testklasse som inneholder en samling av entitycomponents som igjen har en enum i seg. Denne klassen ble opprettet for å teste
 * SKIF-210.
 *
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class BubbleWithList extends AbstractBubbleObject implements StoreTestBubble {
    private String text;

    private Set<BubbleWithListComponent> components;

    public BubbleWithList() {
    }

    public BubbleWithList(BubbleWithList bubbleWithList) {
        this.setId(bubbleWithList.getId());
        this.components = new HashSet<BubbleWithListComponent>();
        for (BubbleWithListComponent component : bubbleWithList.components) {
            BubbleWithListComponent e = new BubbleWithListComponent(component.getId(), component.getComponentName(), component.getaEnumKodeId());
            e.setBubbleWithList(this);
            this.components.add(e);
        }
    }

    @Override
    public BubbleId<?> getId() {
        return super.getId();
    }

    public Set<BubbleWithListComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<BubbleWithListComponent> components) {
        this.components = components;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
