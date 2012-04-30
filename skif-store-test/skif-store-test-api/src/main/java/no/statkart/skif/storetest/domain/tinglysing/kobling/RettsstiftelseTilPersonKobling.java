package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.PersonId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RettsstiftelseTilPersonKobling extends Kobling<RettsstiftelsePersonRolle, PersonId<?>> {
    public PersonId personId;

    public static KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>, RettsstiftelseTilPersonKobling> KOBLING_FACTORY =
            new RettsstiftelsePersonRollePersonIdKoblingFactory();

    public RettsstiftelseTilPersonKobling() {
    }

    public RettsstiftelseTilPersonKobling(RettsstiftelsePersonRolle rolle, PersonId<?> id) {
        this.rolle = rolle;
        this.personId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelsePersonRolle.valueOf(rolle);
    }

    @Override
    protected PersonId<?> getValue() {
        return personId;
    }

    @Override
    protected void setValue(PersonId<?> value) {
        personId = value;
    }

    private static class RettsstiftelsePersonRollePersonIdKoblingFactory implements KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>, RettsstiftelseTilPersonKobling> {
        @Override
        public RettsstiftelseTilPersonKobling create(RettsstiftelsePersonRolle rolle, PersonId<?> targetId) {
            return new RettsstiftelseTilPersonKobling(rolle, targetId);
        }
    }
}
