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

    public Beloep getAarligFesteavgiftIKroner() {
        return aarligFesteavgiftIKroner.isEmpty() ? null : aarligFesteavgiftIKroner.iterator().next();
    }

    public void setAarligFesteavgiftIKroner(Beloep aarligFesteavgiftIKroner) {
        this.aarligFesteavgiftIKroner.clear();
        this.aarligFesteavgiftIKroner.add(aarligFesteavgiftIKroner);
    }

    public int getFestetidIAar() {
        return festetidIAar;
    }

    public void setFestetidIAar(int festetidIAar) {
        this.festetidIAar = festetidIAar;
    }
}
