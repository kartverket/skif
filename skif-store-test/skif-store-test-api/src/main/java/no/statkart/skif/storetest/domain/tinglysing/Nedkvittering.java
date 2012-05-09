package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseBeloepRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public class Nedkvittering extends PaategningForMatrikkelenheter {
    private Set<Beloep> beloepNedkvittertTil = rettsstiftelseBeloep.get(RettsstiftelseBeloepRolle.NEDKVITTERT_TIL);

    @Override
    public NedkvitteringId<?> getId() {
        return (NedkvitteringId<?>) super.getId();
    }

    public Beloep getBeloepNedkvittertTil() {
        return beloepNedkvittertTil.isEmpty() ? null : beloepNedkvittertTil.iterator().next();
    }

    public void setBeloepNedkvittertTil(Beloep beloepNedkvittertTil) {
        this.beloepNedkvittertTil.clear();
        this.beloepNedkvittertTil.add(beloepNedkvittertTil);
    }
}