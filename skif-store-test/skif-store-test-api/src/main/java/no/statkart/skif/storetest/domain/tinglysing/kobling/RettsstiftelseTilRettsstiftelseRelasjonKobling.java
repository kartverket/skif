package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.RettsstiftelseRelasjonId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author rorchr
 */
public class RettsstiftelseTilRettsstiftelseRelasjonKobling extends Kobling<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>> {
    public RettsstiftelseRelasjonId relasjonId;

    public static KoblingFactory<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>, RettsstiftelseTilRettsstiftelseRelasjonKobling> KOBLING_FACTORY =
            new RetttstiftelseTilRettsstiftelseRelasjonKoblingFactory();

    public RettsstiftelseTilRettsstiftelseRelasjonKobling() {
    }

    public RettsstiftelseTilRettsstiftelseRelasjonKobling(RettsstiftelseRelasjonRolle rolle, RettsstiftelseRelasjonId<?> id) {
        this.rolle = rolle;
        this.relasjonId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelseRelasjonRolle.valueOf(rolle);
    }

    @Override
    protected RettsstiftelseRelasjonId<?> getValue() {
        return relasjonId;
    }

    @Override
    protected void setValue(RettsstiftelseRelasjonId<?> value) {
        relasjonId = value;
    }

    private static class RetttstiftelseTilRettsstiftelseRelasjonKoblingFactory implements KoblingFactory<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>, RettsstiftelseTilRettsstiftelseRelasjonKobling> {
        @Override
        public RettsstiftelseTilRettsstiftelseRelasjonKobling create(RettsstiftelseRelasjonRolle rolle, RettsstiftelseRelasjonId<?> targetId) {
            return new RettsstiftelseTilRettsstiftelseRelasjonKobling(rolle, targetId);
        }
    }
}
