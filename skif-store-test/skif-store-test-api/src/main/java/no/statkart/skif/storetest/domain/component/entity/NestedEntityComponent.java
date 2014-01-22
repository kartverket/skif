package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityComponent;

import java.util.Set;

/**
 * EntityComponent som tester nesting. Har et felt {@code nestedComponent} som peken på en annen instans av samme type.
 * Har også et {@code Set} som peker på komponenter av samme type. Ingen instanser skal kunne deles.
 * <p/>
 * <P>Denne klassen har ingen tilbakepeker til owner og kan derfor pekes til fra forskjellige klasser. Der gør at det
 * ikke trengs som mange forskjellige testklasser. I attached state kan rammeverket ikke fange opp hvis komponenten
 * stjæles.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class NestedEntityComponent implements EntityComponent {
    private Long id;
    private String text;
    private NestedEntityComponent nestedComponent;
    private Set<NestedEntityComponent> nestedComponents = Sets.newHashSet();

    public NestedEntityComponent() {
    }

    public NestedEntityComponent(String text) {
        this.text = text;
    }

    public NestedEntityComponent(String text, NestedEntityComponent nestedComponent, Set<NestedEntityComponent> nestedComponents) {
        this.text = text;
        this.nestedComponent = nestedComponent;
        this.setNestedComponents(nestedComponents);
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * Brukes for testing av stjålne id
     */
    public void setIdForTesting(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NestedEntityComponent getNestedComponent() {
        return nestedComponent;
    }

    public void setNestedComponent(NestedEntityComponent nestedComponent) {
        this.nestedComponent = nestedComponent;
    }

    public Set<NestedEntityComponent> getNestedComponents() {
        return nestedComponents;
    }

    public void setNestedComponents(Set<NestedEntityComponent> nestedComponents) {
        Components.setFrom(this.nestedComponents, nestedComponents);
    }

    public void removeHibernatePersistenceSet() {
        if (nestedComponent!=null) nestedComponent.removeHibernatePersistenceSet();
        nestedComponents = Sets.newHashSet(nestedComponents);
        for (NestedEntityComponent component : nestedComponents) {
            component.removeHibernatePersistenceSet();
        }
    }
}
