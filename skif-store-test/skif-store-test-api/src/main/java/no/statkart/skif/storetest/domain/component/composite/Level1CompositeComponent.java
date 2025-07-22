package no.statkart.skif.storetest.domain.component.composite;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import no.statkart.skif.store.AbstractCompositeBubbleComponent;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.CompositeComponentWithCollections;
import no.statkart.skif.store.ValueObjects;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level1CompositeComponent extends  AbstractCompositeBubbleComponent<BubbleWithCompositeComponent> implements CompositeComponentWithCollections {
    private String text;
    private BeloepValueObject beloep;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();

    private Level2CompositeComponent level2Component;

    public Level1CompositeComponent() {
        setLevel2Component(new Level2CompositeComponent());
    }

    public Level1CompositeComponent(String text, BeloepValueObject beloep, ImmutableSet<BeloepValueObject> beloepSet) {
        setText(text);
        setBeloep(beloep);
        setBeloepSet(beloepSet);
        setLevel2Component(new Level2CompositeComponent());
    }

    @Override
    public void onSetCompositeRootOwner() {
        onSetCompositeRootOwner(level2Component);
    }

    public void clear() {
        setText(null);
        setBeloep(null);
        beloepSet.clear();
        level2Component.clear();
    }

    @Nullable
    public Level2CompositeComponent getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable Level2CompositeComponent level2Component) {
        this.level2Component = Components.checkSetComponent(this, this.level2Component, level2Component);
    }

    @Override
    public boolean isNullComponent() {
        return this.text == null
                && this.beloep == null
                && Components.isNullComponent(this.level2Component);
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
