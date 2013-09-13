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
public class Level2CompositeComponent implements CompositeComponent<Level1CompositeComponent>, CompositeComponentWithCollections {
    private Level1CompositeComponent owner;
    private String text;
    private BeloepValueObject belop;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();


    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public Level2CompositeComponent() {
    }

    public Level2CompositeComponent(String text, BeloepValueObject beloep, ImmutableSet<BeloepValueObject> beloepSet) {
        setText(text);
        setBelop(beloep);
        setBeloepSet(beloepSet);
    }

    @Override
    public Level1CompositeComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Level1CompositeComponent owner) {
        this.owner = Components.checkSetOwner(
                this,
                this.owner,
                owner,
                new OwnerCheck<Level1CompositeComponent, Level2CompositeComponent>() {
                    public boolean apply(Level1CompositeComponent owner, Level2CompositeComponent child) {
                        return owner.getLevel2Component()==child;
                    }
                }
        );
    }

    @Override
    public boolean isNullComponent() {
        return this.text==null
                && this.belop==null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BeloepValueObject getBelop() {
        return belop;
    }

    public void setBelop(BeloepValueObject belop) {
        this.belop = belop;
    }

    public Set<BeloepValueObject> getBeloepSet() {
        return beloepSet;
    }

    public void setBeloepSet(Set<BeloepValueObject> beloepSet) {
        ValueObjects.setFrom(this.beloepSet, beloepSet);
    }
}
