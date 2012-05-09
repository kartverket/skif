package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseBeloepRolle;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelsePersonRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public abstract class PrioritetIkkeTinglystDokument extends PaategningForMatrikkelenheter {
    private Set<Beloep> beloep = rettsstiftelseBeloep.get(RettsstiftelseBeloepRolle.BELOEP);
    private Set<PersonId<?>> panthavere = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.PANTHAVER_AKTIV);

    @Override
    public PrioritetIkkeTinglystDokumentId<?> getId() {
        return (PrioritetIkkeTinglystDokumentId<?>) super.getId();
    }

    public Beloep getBeloep() {
        return beloep.isEmpty() ? null : beloep.iterator().next();
    }

    public void setBeloep(Beloep beloep) {
        this.beloep.clear();
        this.beloep.add(beloep);
    }

    public Set<PersonId<?>> getPanthavere() {
        return panthavere;
    }

    public void setPanthavere(Set<PersonId<?>> panthavere) {
        this.panthavere.clear();
        this.panthavere.addAll(panthavere);
    }
}
