package no.statkart.skif.storetest.domain.multikobling.entity;

import no.statkart.skif.store.multikobling.EntityKobling;
import no.statkart.skif.store.multikobling.Kobling;

/**
 * Koblingsklasse for {@link EntityInMultikobling}. Denne klassen er et eksempel på en kobling som wrapper
 * en {@link no.statkart.skif.store.EntityComponent}. Klassen forwarder alle kall til {@code value}.
 *
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public class EntityInMultikoblingKobling extends EntityKobling<String, EntityInMultikobling> {
    private static final long serialVersionUID = 1L;

    private EntityInMultikobling value;

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public EntityInMultikoblingKobling() {
        setValue(new EntityInMultikobling());
    }

    public EntityInMultikoblingKobling(String rolle, EntityInMultikobling value) {
        super(rolle, value);
    }

    @Override
    protected EntityInMultikobling getValue() {
        return value;
    }

    @Override
    protected void setValue(EntityInMultikobling value) {
        this.value = value;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private Long getId() {
        return value.getId();
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        value.setId(id);
    }

    private BubbleWithEntityInMultikobling getOwner() {
        return value.getOwner();
    }

    private void setOwner(BubbleWithEntityInMultikobling owner) {
        value.setOwner(owner);
    }

    public String getTekst() {
        return value.getTekst();
    }

    public void setTekst(String tekst) {
        value.setTekst(tekst);
    }
}
