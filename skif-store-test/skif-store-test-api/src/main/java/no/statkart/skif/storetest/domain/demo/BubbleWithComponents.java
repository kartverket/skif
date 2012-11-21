package no.statkart.skif.storetest.domain.demo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class BubbleWithComponents extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private Set<BubbleWithComponentsComponent> components = new HashSet<BubbleWithComponentsComponent>();

    @Override
    public BubbleWithComponentsId<?> getId() {
        return (BubbleWithComponentsId<?>) super.getId();
    }

    public Set<BubbleWithComponentsComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<BubbleWithComponentsComponent> components) {
        this.components = components;
    }
}
