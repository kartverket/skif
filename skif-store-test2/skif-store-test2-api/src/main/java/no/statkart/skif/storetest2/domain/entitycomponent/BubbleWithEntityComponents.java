package no.statkart.skif.storetest2.domain.entitycomponent;

import no.statkart.skif.storetest2.domain.AbstractStoreTest2Bubble;

import java.util.HashSet;
import java.util.Set;

/**
 * Tester måter entitycomponents kan brukes på.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class BubbleWithEntityComponents extends AbstractStoreTest2Bubble {
    private static final long serialVersionUID = 1L;

    private EntityComponent mainEntityComponent;
    private Set<EntityComponent> secondaryEntityComponents = new HashSet<EntityComponent>();

    @Override
    public BubbleWithEntityComponentsId<?> getId() {
        return (BubbleWithEntityComponentsId<?>) super.getId();
    }

    public EntityComponent getMainEntityComponent() {
        return mainEntityComponent;
    }

    public void setMainEntityComponent(EntityComponent mainEntityComponent) {
        this.mainEntityComponent = mainEntityComponent;
    }

    public Set<EntityComponent> getSecondaryEntityComponents() {
        return secondaryEntityComponents;
    }

    public void setSecondaryEntityComponents(Set<EntityComponent> secondaryEntityComponents) {
        this.secondaryEntityComponents = secondaryEntityComponents;
    }
}
