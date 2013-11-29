package no.statkart.skif.storetest.domain.component.composite;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level2CompositeComponent implements CompositeComponent<BubbleWithCompositeComponent, Level1CompositeComponent>, CompositeComponentWithCollections {
    private Level1CompositeComponent owner;
    private String text;
    private BeloepValueObject beloep;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();


    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public Level2CompositeComponent() {
    }

    @Override
    public void onSetCompositeRootOwner() {
    }

    public Level2CompositeComponent(String text, BeloepValueObject beloep, ImmutableSet<BeloepValueObject> beloepSet) {
        setText(text);
        setBeloep(beloep);
        setBeloepSet(beloepSet);
    }

    public void clear() {
        setText(null);
        setBeloep(null);
        beloepSet.clear();
    }

    @Override
    public Level1CompositeComponent getOwner() {
        return owner;
    }

    @Override
    public BubbleWithCompositeComponent getCompositeRootOwner() {
        return getOwner().getCompositeRootOwner();
    }

    @Override
    public void setOwner(Level1CompositeComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    @Override
    public boolean isNullComponent() {
        return this.text == null
                && this.beloep == null;
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
}
