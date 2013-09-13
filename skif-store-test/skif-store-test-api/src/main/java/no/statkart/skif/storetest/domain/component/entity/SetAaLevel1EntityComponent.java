package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * EntityComponent som har {@code SetAaEntityComponent} som owner
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class SetAaLevel1EntityComponent implements EntityComponentWithOwnerReferance<SetAaEntityComponent> {
    private Long id;
    private SetAaEntityComponent owner;
    private String text;
    private SetAaLevel2EntityComponent level2Component;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();


    public SetAaLevel1EntityComponent() {

    }
    public SetAaLevel1EntityComponent(String text, SetAaLevel2EntityComponent level2Component) {
        setText(text);
        setLevel2Component(level2Component);
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    @Override
    public SetAaEntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(SetAaEntityComponent owner) {
        this.owner = Components.checkSetOwner(
                this,
                this.owner,
                owner,
                new OwnerCheck<SetAaEntityComponent, SetAaLevel1EntityComponent>() {
                    public boolean apply(SetAaEntityComponent owner, SetAaLevel1EntityComponent child) {
                        return owner.getLevel1Component()==child;
                    }
                }
        );
    }

    @Nullable
    public SetAaLevel2EntityComponent getLevel2Component() {
        return level2Component;
    }

    public void setLevel2Component(@Nullable SetAaLevel2EntityComponent level2Component) {
        this.level2Component = Components.checkSetComponentWithOwner(this.level2Component, level2Component);
        Components.setOwner(this.level2Component, this);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<BeloepValueObject> getBeloepSet() {
        return beloepSet;
    }

    public void setBeloepSet(Set<BeloepValueObject> beloepSet) {
        ValueObjects.setFrom(this.beloepSet, beloepSet);
    }

    public void removeHibernatePersistenceSet() {
        if (level2Component!=null) level2Component.removeHibernatePersistenceSet();
        beloepSet = Sets.newHashSet(beloepSet);
    }
}
