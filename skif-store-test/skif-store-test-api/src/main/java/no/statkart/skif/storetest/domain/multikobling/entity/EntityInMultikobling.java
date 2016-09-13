package no.statkart.skif.storetest.domain.multikobling.entity;

import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;
import no.statkart.skif.store.Components;

/**
 * En {@link no.statkart.skif.store.EntityComponent} som er element i en {@link no.statkart.skif.store.multikobling.Multikobling}. Se
 * {@link BubbleWithEntityInMultikobling}.
 *
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public class EntityInMultikobling extends AbstractEntityBubbleComponentWithOwner<BubbleWithEntityInMultikobling> {
    private Long id;
    private BubbleWithEntityInMultikobling owner;
    private String tekst;

    public EntityInMultikobling() {
    }

    public Long getId() {
        return id;
    }

    public EntityInMultikobling(String tekst) {
        this.tekst = tekst;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    protected void setId(Long id) {
        this.id = id;
    }

    /**
     * Hjelpemetode for chaining i tester
     */
    public EntityInMultikobling withId(long id) {
        setId(id);
        return this;
    }

    @Override
    public BubbleWithEntityInMultikobling getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithEntityInMultikobling owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
    }

    /**
     * Hjelpemetode for chaining i tester
     */
    public EntityInMultikobling withOwner(BubbleWithEntityInMultikobling owner) {
        setOwner(owner);
        return this;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
}
