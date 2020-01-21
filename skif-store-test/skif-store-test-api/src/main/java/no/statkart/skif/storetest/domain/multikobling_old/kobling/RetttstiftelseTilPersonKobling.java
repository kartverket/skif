package no.statkart.skif.storetest.domain.multikobling_old.kobling;

import no.statkart.skif.store.multikobling.DefaultKoblingFactory;
import no.statkart.skif.store.multikobling.Kobling;
import no.statkart.skif.store.multikobling.KoblingFactory;
import no.statkart.skif.storetest.domain.multikobling_old.PersonId;
import no.statkart.skif.store.multikobling.Multikobling;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RetttstiftelseTilPersonKobling extends Kobling<RettsstiftelsePersonRolle, PersonId<?>> {
    private PersonId<?> personId;

    public static KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>, RetttstiftelseTilPersonKobling> KOBLING_FACTORY =
            DefaultKoblingFactory.create(RetttstiftelseTilPersonKobling.class);

    /** Brukes av hibernate */
    public RetttstiftelseTilPersonKobling() {
    }

    public RetttstiftelseTilPersonKobling(RettsstiftelsePersonRolle rolle, PersonId<?> id) {
        super(rolle, id);
    }

    /** Callback funksjon som brukes av {@link Multikobling} [@link Kobling} */
    public PersonId<?> getValue() {
        return personId;
    }

    /** Callback funksjon som brukes av {@link Multikobling} [@link Kobling} */
    public void setValue(PersonId<?> personId) {
        this.personId = personId;
    }

    public PersonId<?> getPersonId() {
        return personId;
    }

    public void setPersonId(PersonId<?> personId) {
        this.personId = personId;
    }

}
