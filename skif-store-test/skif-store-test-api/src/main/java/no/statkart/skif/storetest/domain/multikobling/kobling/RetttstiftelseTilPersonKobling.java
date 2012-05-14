package no.statkart.skif.storetest.domain.multikobling.kobling;

import no.statkart.skif.store.multikobling.Kobling;
import no.statkart.skif.store.multikobling.KoblingFactory;
import no.statkart.skif.storetest.domain.multikobling.PersonId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RetttstiftelseTilPersonKobling extends Kobling<RettsstiftelsePersonRolle, PersonId<?>> {

    public static KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>, RetttstiftelseTilPersonKobling> KOBLING_FACTORY =
            new RettsstiftelsePersonRollePersonIdKoblingFactory();

    public RetttstiftelseTilPersonKobling() {
    }

    public RetttstiftelseTilPersonKobling(RettsstiftelsePersonRolle rolle, PersonId<?> id) {
        super(rolle,id);
    }

    /**
     * Brukes av hibernate for mapping av String til rolle. Trengs kun for å støtte hibernate 3.2.6. I 3.4.10 kan man bruke hibernates støtte for enum
     * @param rolle
     */
    private void setRolle(String rolle) {
        this.rolle = RettsstiftelsePersonRolle.valueOf(rolle);
    }

    private static class RettsstiftelsePersonRollePersonIdKoblingFactory implements KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>, RetttstiftelseTilPersonKobling> {
        @Override
        public RetttstiftelseTilPersonKobling create(RettsstiftelsePersonRolle rolle, PersonId<?> targetId) {
            return new RetttstiftelseTilPersonKobling(rolle, targetId);
        }
    }
}
