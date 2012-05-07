package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseBeloepRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public class NyeVilkaarIFesteavtale extends PaategningForMatrikkelenheter {
    private Set<Beloep> aarligFesteavgiftIKroner = rettsstiftelseBeloep.get(RettsstiftelseBeloepRolle.AVGIFT);
    private int festetidIAar;

    @Override
    public NyeVilkaarIFesteavtaleId<?> getId() {
        return (NyeVilkaarIFesteavtaleId<?>) super.getId();
    }
}
