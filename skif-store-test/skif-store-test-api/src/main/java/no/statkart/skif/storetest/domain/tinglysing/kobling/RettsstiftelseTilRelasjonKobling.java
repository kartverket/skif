package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.RettsstiftelseRelasjonId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author rorchr
 */
public class RettsstiftelseTilRelasjonKobling extends Kobling<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>> {
    public RettsstiftelseRelasjonId relasjonId;

    public static KoblingFactory<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>, RettsstiftelseTilRelasjonKobling> KOBLING_FACTORY =
            new RetttstiftelseTilRelasjonKoblingFactory();

    public RettsstiftelseTilRelasjonKobling() {
    }

    public RettsstiftelseTilRelasjonKobling(RettsstiftelseRelasjonRolle rolle, RettsstiftelseRelasjonId<?> id) {
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

    private static class RetttstiftelseTilRelasjonKoblingFactory implements KoblingFactory<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>, RettsstiftelseTilRelasjonKobling> {
        @Override
        public RettsstiftelseTilRelasjonKobling create(RettsstiftelseRelasjonRolle rolle, RettsstiftelseRelasjonId<?> targetId) {
            return new RettsstiftelseTilRelasjonKobling(rolle, targetId);
        }
    }
}
