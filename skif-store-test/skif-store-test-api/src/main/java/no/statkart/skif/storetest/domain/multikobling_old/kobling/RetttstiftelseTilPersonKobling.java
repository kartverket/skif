package no.statkart.skif.storetest.domain.multikobling_old.kobling;

import no.statkart.skif.store.multikobling.DefaultKoblingFactory;
import no.statkart.skif.store.multikobling.Kobling;
import no.statkart.skif.store.multikobling.KoblingFactory;
import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest.domain.multikobling_old.PersonId;

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

    /**
     * Brukes av hibernate for mapping av String til rolle. Trengs kun for å støtte hibernate 3.2.6. I 3.4.10 kan man bruke hibernates støtte for enum
     */
    private void setRolleFix(String rolle) {
        setRolle(RettsstiftelsePersonRolle.valueOf(rolle));
    }

    /**
     * Brukes av hibernate for mapping av String til rolle. Trengs kun for å støtte hibernate 3.2.6. I 3.4.10 kan man bruke hibernates støtte for enum
     */
    private String getRolleFix() {
        return getRolle().toString();
    }
}
