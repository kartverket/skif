package no.statkart.skif.storetest.domain.component.entity;

import com.google.common.collect.Sets;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityComponentWithOwnerReference;
import no.statkart.skif.store.ValueObjects;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;

import java.util.Set;

/**
 * EntityComponent som har {@code SetAaLevel1EntityComponent} som owner.
 *
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class SetAaLevel2EntityComponent implements EntityComponentWithOwnerReference<SetAaLevel1EntityComponent> {
    private Long id;
    private SetAaLevel1EntityComponent owner;
    private String text;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();


    public SetAaLevel2EntityComponent() {
    }

    public SetAaLevel2EntityComponent(String text) {
        setText(text);
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
    @Override
    public SetAaLevel1EntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(SetAaLevel1EntityComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
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
        beloepSet = Sets.newHashSet(beloepSet);
    }
}
