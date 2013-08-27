package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityBubbleComponent;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * EntityComponent som har {@code BubbleWithEntityComponent} som owner
 *
 * <P>Komponenten har en ident ved navn {@code ident} som kan endres.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class Level1EntityComponent implements EntityBubbleComponent<BubbleWithEntityComponent> {
    private Long id;
    private BubbleWithEntityComponent owner;
    private String text;
    private BeloepValueObject beloep;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();
    private Level2EntityComponent level2Component;

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
        this.owner = Components.checkSetOwner(
                this,
                this.owner,
                owner,
                new Function<BubbleWithEntityComponent, Level1EntityComponent>() {
                    public Level1EntityComponent apply(BubbleWithEntityComponent owner) {
                        return owner.getLevel1Component();
                    }
                }
        );
    }

    @Nullable
    public Level2EntityComponent getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable Level2EntityComponent level2Component) {
        this.level2Component = Components.checkSetComponentWithOwner(this.level2Component, level2Component);
        Components.setOwner(this.level2Component, this);
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
        this.beloepSet = beloepSet;
    }
}
