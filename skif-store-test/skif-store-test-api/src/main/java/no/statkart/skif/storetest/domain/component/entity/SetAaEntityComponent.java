package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.ValueObjects;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import java.util.Set;

/**
 * Entity som inngår i et set A som ligger i BubbleWithEntityComponent.
 *
 * <P>Komponenten har en ident {@code ident} som skal kunne endres mens komponenten inngår i et Set.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class SetAaEntityComponent extends AbstractEntityBubbleComponentWithOwner<BubbleWithEntityComponent> {
    private Long id;
    private transient BubbleWithEntityComponent owner;
    private int ident;
    private String text;
    private SetAaLevel1EntityComponent level1Component;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();
    private NestedEntityComponent nestedComponent;

    public SetAaEntityComponent() {
    }

    public SetAaEntityComponent(int ident, String text) {
        this.ident=ident;
        this.text = text;
    }

    public SetAaEntityComponent(int ident, String text, SetAaLevel1EntityComponent level1Component) {
        setIdent(ident);
        setText(text);
        setLevel1Component(level1Component);
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    @Override
    public BubbleWithEntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithEntityComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SetAaLevel1EntityComponent getLevel1Component() {
        return level1Component;
    }

    public void setLevel1Component(SetAaLevel1EntityComponent level1Component) {
        this.level1Component = Components.checkSetComponent(this, this.level1Component, level1Component);
    }

    public Set<BeloepValueObject> getBeloepSet() {
        return beloepSet;
    }

    public void setBeloepSet(Set<BeloepValueObject> beloepSet) {
        ValueObjects.setFrom(this.beloepSet, beloepSet);
    }

    public NestedEntityComponent getNestedComponent() {
        return nestedComponent;
    }

    public void setNestedComponent(NestedEntityComponent nestedComponent) {
        this.nestedComponent = nestedComponent;
    }

    public void removeHibernatePersistenceSet() {
        beloepSet = Sets.newHashSet(beloepSet);
        if (level1Component!=null)level1Component.removeHibernatePersistenceSet();
        if (nestedComponent!=null)nestedComponent.removeHibernatePersistenceSet();
    }

    private Object readResolve() {
        if (level1Component != null) level1Component.setOwner(this);
        return this;
    }
}
