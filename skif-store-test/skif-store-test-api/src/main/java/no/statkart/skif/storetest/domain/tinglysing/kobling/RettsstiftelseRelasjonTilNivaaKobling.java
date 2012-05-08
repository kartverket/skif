package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.NivaaIMatrikkelenhetId;
import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

/**
 * @author rorchr
 */
public class RettsstiftelseRelasjonTilNivaaKobling extends Kobling<RettsstiftelseRelasjonNivaaRolle, NivaaIMatrikkelenhetId<?>> {
    public NivaaIMatrikkelenhetId nivaaIMatrikkelenhetId;

    public static KoblingFactory<RettsstiftelseRelasjonNivaaRolle, NivaaIMatrikkelenhetId<?>, RettsstiftelseRelasjonTilNivaaKobling> KOBLING_FACTORY =
            new RettsstiftelseRelasjonTilNivaaIMatrikkelenhetKoblingFactory();

    public RettsstiftelseRelasjonTilNivaaKobling() {
    }

    public RettsstiftelseRelasjonTilNivaaKobling(RettsstiftelseRelasjonNivaaRolle rolle, NivaaIMatrikkelenhetId<?> id) {
        this.rolle = rolle;
        this.nivaaIMatrikkelenhetId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelseRelasjonNivaaRolle.valueOf(rolle);
    }

    @Override
    protected NivaaIMatrikkelenhetId<?> getValue() {
        return nivaaIMatrikkelenhetId;
    }

    @Override
    protected void setValue(NivaaIMatrikkelenhetId<?> value) {
        nivaaIMatrikkelenhetId = value;
    }

    private static class RettsstiftelseRelasjonTilNivaaIMatrikkelenhetKoblingFactory
            implements KoblingFactory<RettsstiftelseRelasjonNivaaRolle, NivaaIMatrikkelenhetId<?>, RettsstiftelseRelasjonTilNivaaKobling> {
        @Override
        public RettsstiftelseRelasjonTilNivaaKobling create(RettsstiftelseRelasjonNivaaRolle rolle, NivaaIMatrikkelenhetId<?> targetId) {
            return new RettsstiftelseRelasjonTilNivaaKobling(rolle, targetId);
        }
    }
}
