package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.RettsstiftelseId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author Knut Inge Bøe
 */
public class RettsstiftelseTilRettsstiftelseKobling extends Kobling<RettsstiftelseRettsstiftelseRolle, RettsstiftelseId<?>> {
    public RettsstiftelseId rettsstiftelseId;

    public static KoblingFactory<RettsstiftelseRettsstiftelseRolle, RettsstiftelseId<?>, RettsstiftelseTilRettsstiftelseKobling> KOBLING_FACTORY =
            new RettsstiftelseRettsstiftelseRolleRettsstiftelseIdKoblingFactory();

    public RettsstiftelseTilRettsstiftelseKobling() {
    }

    public RettsstiftelseTilRettsstiftelseKobling(RettsstiftelseRettsstiftelseRolle rolle, RettsstiftelseId<?> id) {
        this.rolle = rolle;
        this.rettsstiftelseId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelseRettsstiftelseRolle.valueOf(rolle);
    }

    @Override
    protected RettsstiftelseId<?> getValue() {
        return rettsstiftelseId;
    }

    @Override
    protected void setValue(RettsstiftelseId<?> value) {
        rettsstiftelseId = value;
    }

    private static class RettsstiftelseRettsstiftelseRolleRettsstiftelseIdKoblingFactory implements KoblingFactory<RettsstiftelseRettsstiftelseRolle, RettsstiftelseId<?>, RettsstiftelseTilRettsstiftelseKobling> {
        @Override
        public RettsstiftelseTilRettsstiftelseKobling create(RettsstiftelseRettsstiftelseRolle rolle, RettsstiftelseId<?> targetId) {
            return new RettsstiftelseTilRettsstiftelseKobling(rolle, targetId);
        }
    }
}

