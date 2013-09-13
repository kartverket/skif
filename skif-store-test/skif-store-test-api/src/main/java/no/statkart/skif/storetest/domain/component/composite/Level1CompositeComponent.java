package no.statkart.skif.storetest.domain.component.composite;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Level1CompositeComponent implements CompositeBubbleComponent<BubbleWithCompositeComponent>, CompositeComponentWithCollections {
    private BubbleWithCompositeComponent owner;
    private String text;
    private BeloepValueObject belop;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();

    private Level2CompositeComponent level2Component;

    public Level1CompositeComponent() {
    }

    public Level1CompositeComponent(String text, BeloepValueObject beloep, ImmutableSet<BeloepValueObject> beloepSet ) {
        setText(text);
        setBelop(beloep);
        setBeloepSet(beloepSet);
    }

    @Override
    public BubbleWithCompositeComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithCompositeComponent owner) {
       this.owner = Components.checkSetOwner(
               this,
               this.owner,
               owner,
               new OwnerCheck<BubbleWithCompositeComponent, Level1CompositeComponent>() {
                   public boolean apply(BubbleWithCompositeComponent owner, Level1CompositeComponent child) {
                       return owner.getLevel1Component()==child;
                   }
               }
       );
    }

    @Nullable
    public Level2CompositeComponent getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable Level2CompositeComponent level2Component) {
        this.level2Component = Components.checkSetComponentWithOwner(this.level2Component, level2Component);
        Components.setOwner(this.level2Component, this);
    }

    @Override
    public boolean isNullComponent() {
        return this.text == null
                && this.belop == null
                && this.level2Component.isNullComponent();
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
