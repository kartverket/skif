package no.statkart.skif.store.multikobling;

import no.statkart.skif.store.EntityComponent;

import java.util.Objects;

/**
 * Koblingsklasse for {@link Multikobling} hvor {@code V} er en {@link EntityComponent} med unik id for hver instans.
 * Dvs., to instanser av {@code V} er kun like hvis de har samme id. Rolle {@code R} er derfor ikke med i {@code equals}
 * og {@code hashCode} for koblingsklassen. Hver instans av {@code V} kan kun tilhører en rolle om gangen.
 * <p>
 * For at {@code remove}- og {@code add}-operasjoner skal virker korrekt sammen med Hibernate når {@code V}
 * skal skifte rolle innenfor en {@code Multikobling} er det nødvendig å konfigurerer Hibernate mappingen for
 * multikoblingen med {@code collection-type="no.statkart.skif.store.multikobling.MultikoblingPersistentSetType"}.
 * Denne collection typen sikre at entity-koblingsinstansen som fjernes ved {@code remove} blir gjenfunnet og brukt
 * av {@code add}-operasjonen. Manglende konfigurasjon av collection-type kan føre til at Hibernate kaster exception
 * {@code 'org.hibernate.NonUniqueObjectException: a different object with the same identifier value was already
 * associated with the session'}.
 * <p>
 * Ved endring av rolle for {@code V} må koden skrives slik at det ikke utføres et Hibernate {@code flush}-kall mellom
 * {@code remove}- og {@code add}-operasjonene. En flush der vil føre til at koblingen blir orphan og dermed slettet.
 * Etterfølgende  {@code add}-operasjonen vil feile med exception {@code 'org.hibernate.StaleObjectStateException:
 * Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)'}.
 *
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public abstract class EntityKobling<R,V extends EntityComponent> extends Kobling<R,V> {
    private static final long serialVersionUID = 1L;

    protected EntityKobling() {
    }

    protected EntityKobling(R rolle, V value) {
        super(rolle, value);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKobling<?, ?> kobling = (EntityKobling<?, ?>) o;
        return Objects.equals(getValue(), kobling.getValue());
    }

    @Override
    public final int hashCode() {
        return getValue().hashCode();
    }
}
