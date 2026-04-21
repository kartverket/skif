package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;

import jakarta.annotation.Nullable;
import java.util.Set;

/**
 * Boble som har entity og set av entities i nestede nivåer.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class BubbleWithEntityComponent extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    /* Angir logisk nummer på boblen innen for et testset*/
    private int nr;
    /* En tekst som beskriver boblen */
    private String text;
    private Level1EntityComponent level1Component;
    private final Set<SetAaEntityComponent> aaComponents = Components.newSet(this);

    public BubbleWithEntityComponent() {
    }

    public BubbleWithEntityComponent(BubbleWithCompositeComponentId<?> id) {
        super(id);
    }

    public BubbleWithEntityComponent(BubbleWithCompositeComponentId id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public BubbleWithEntityComponentId<?> getId() {
        return (BubbleWithEntityComponentId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Nullable
    public Level1EntityComponent getLevel1Component() {
        return level1Component;
    }

    public void setLevel1Component(Level1EntityComponent level1Component) {
        this.level1Component = Components.checkSetComponent(this, this.level1Component, level1Component);
    }

    public Set<SetAaEntityComponent> getAaComponents() {
        return aaComponents;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private Set<SetAaEntityComponent> getAaComponentsSet() {
        return Components.getDelegate(aaComponents);

    }

    public void setAaComponents(Set<SetAaEntityComponent> aaComponents) {
        Components.setFrom(this.aaComponents, aaComponents);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setAaComponentsSet(Set<SetAaEntityComponent> aaComponents) {
        Components.setDelegate(this.aaComponents, aaComponents);
    }

    /**
     * Hjelpemetode som brukes i testing for av bobler i detatched state. Metoden fjerner al bruk av PersistenceSet
     * i objektet slik at Hibernate ikke kan utnytte informasjon om hvilke elementer som er endret i settet mens
     * objektet var detatched. Dette simulerer hvordan objektet vil se ut for Hibernate hvis det blir mappet via WS-mapping.
     */
    public void removeHibernatePersistenceSet() {
        if (level1Component!=null) level1Component.removeHibernatePersistenceSet();
        Components.setDelegate(aaComponents, Sets.newHashSet(aaComponents));
        for (SetAaEntityComponent aaComponent : aaComponents) {
               aaComponent.removeHibernatePersistenceSet();
        }
    }
}
