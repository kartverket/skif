package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseBeloepRolle;

import java.util.Date;
import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public class NyeVilkaarILeie extends PaategningForMatrikkelenheter {
    private Set<Beloep> leiebeloep = rettsstiftelseBeloep.get(RettsstiftelseBeloepRolle.LEIEBELOEP);
    private Date leieFraDato;
    private int leietid; // TODO: egendefinert?

    @Override
    public NyeVilkaarILeieId<?> getId() {
        return (NyeVilkaarILeieId<?>) super.getId();
    }

    public Beloep getLeiebeloep() {
        return leiebeloep.isEmpty() ? null : leiebeloep.iterator().next();
    }

    public void setLeiebeloep(Beloep leiebeloep) {
        this.leiebeloep.clear();
        this.leiebeloep.add(leiebeloep);
    }

    public Date getLeieFraDato() {
        return leieFraDato;
    }

    public void setLeieFraDato(Date leieFraDato) {
        this.leieFraDato = leieFraDato;
    }

    public int getLeietid() {
        return leietid;
    }

    public void setLeietid(int leietid) {
        this.leietid = leietid;
    }
}
