package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.AndelIMatrikkelenhetId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RettsstiftelseTilAndelKobling extends Kobling<RettsstiftelseAndelRolle, AndelIMatrikkelenhetId<?>> {
    public AndelIMatrikkelenhetId andelId;

    public static KoblingFactory<RettsstiftelseAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseTilAndelKobling> KOBLING_FACTORY =
            new RetttstiftelseTilAndelKoblingFactory();

    public RettsstiftelseTilAndelKobling() {
    }

    public RettsstiftelseTilAndelKobling(RettsstiftelseAndelRolle rolle, AndelIMatrikkelenhetId<?> id) {
        this.rolle = rolle;
        this.andelId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelseAndelRolle.valueOf(rolle);
    }

    @Override
    protected AndelIMatrikkelenhetId<?> getValue() {
        return andelId;
    }

    @Override
    protected void setValue(AndelIMatrikkelenhetId<?> value) {
        andelId = value;
    }

    private static class RetttstiftelseTilAndelKoblingFactory implements KoblingFactory<RettsstiftelseAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseTilAndelKobling> {
        @Override
        public RettsstiftelseTilAndelKobling create(RettsstiftelseAndelRolle rolle, AndelIMatrikkelenhetId<?> targetId) {
            return new RettsstiftelseTilAndelKobling(rolle, targetId);
        }
    }
}
