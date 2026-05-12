package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import jakarta.annotation.Nullable;
import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.store.ValueObjects;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import java.util.Set;

/**
 * EntityComponent som har {@code BubbleWithEntityComponent} som owner
 *
 * <P>Komponenten har en ident ved navn {@code ident} som kan endres.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level1EntityComponent extends AbstractEntityBubbleComponentWithOwner<BubbleWithEntityComponent> implements InverseRelationParticipation {
    private Long id;
    private BubbleWithEntityComponent owner;
    private String text;
    private BeloepValueObject beloep;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();
    private Level2EntityComponent level2Component;

    public Long getId() {
        return id;
    }

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
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

    @Nullable
    public Level2EntityComponent getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable Level2EntityComponent level2Component) {
        this.level2Component = Components.checkSetComponent(this, this.level2Component, level2Component);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public BeloepValueObject getBeloep() {
        return beloep;
    }

    public void setBeloep(BeloepValueObject beloep) {
        this.beloep = beloep;
    }

    public Set<BeloepValueObject> getBeloepSet() {
        return beloepSet;
    }

    public void setBeloepSet(Set<BeloepValueObject> beloepSet) {
        ValueObjects.setFrom(this.beloepSet, beloepSet);
    }

    public void removeHibernatePersistenceSet() {
        beloepSet = Sets.newHashSet(beloepSet);
        if (level2Component!=null) level2Component.removeHibernatePersistenceSet();

    }
}
