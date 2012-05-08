package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.AndelIMatrikkelenhetId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author rorchr
 */
public class RettsstiftelseRelasjonTilAndelKobling extends Kobling<RettsstiftelseRelasjonAndelRolle, AndelIMatrikkelenhetId<?>> {
    public AndelIMatrikkelenhetId andelId;

    public static KoblingFactory<RettsstiftelseRelasjonAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseRelasjonTilAndelKobling> KOBLING_FACTORY =
            new RetttstiftelseRelasjonTilAndelKoblingFactory();

    public RettsstiftelseRelasjonTilAndelKobling() {
    }

    public RettsstiftelseRelasjonTilAndelKobling(RettsstiftelseRelasjonAndelRolle rolle, AndelIMatrikkelenhetId<?> id) {
        this.rolle = rolle;
        this.andelId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelseRelasjonAndelRolle.valueOf(rolle);
    }

    @Override
    protected AndelIMatrikkelenhetId<?> getValue() {
        return andelId;
    }

    @Override
    protected void setValue(AndelIMatrikkelenhetId<?> value) {
        andelId = value;
    }

    private static class RetttstiftelseRelasjonTilAndelKoblingFactory implements KoblingFactory<RettsstiftelseRelasjonAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseRelasjonTilAndelKobling> {
        @Override
        public RettsstiftelseRelasjonTilAndelKobling create(RettsstiftelseRelasjonAndelRolle rolle, AndelIMatrikkelenhetId<?> targetId) {
            return new RettsstiftelseRelasjonTilAndelKobling(rolle, targetId);
        }
    }
}
