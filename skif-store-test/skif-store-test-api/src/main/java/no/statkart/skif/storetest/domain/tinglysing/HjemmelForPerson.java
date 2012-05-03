package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseAndelRolle;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseBeloepRolle;

import java.util.Set;

public class HjemmelForPerson extends Hjemmel {
    private Set<AndelIMatrikkelenhetId<?>> kjoeptAndelIds = rettsstiftelseAndelIdsKoblinger.get(RettsstiftelseAndelRolle.KJOEPT);
    private Set<AndelIMatrikkelenhetId<?>> solgtAndelIds = rettsstiftelseAndelIdsKoblinger.get(RettsstiftelseAndelRolle.SOLGT);
    private Set<Beloep> vederlag = rettsstiftelseBeloep.get(RettsstiftelseBeloepRolle.VEDERLAG);
    private String omsetningstype = null; // TODO: Implementer når Christian har sjekka inn støtte for koder

    @Override
    public HjemmelForPersonId<?> getId() {
        return (HjemmelForPersonId<?>) super.getId();
    }

    public Set<AndelIMatrikkelenhetId<?>> getKjoeptAndelIds() {
        return kjoeptAndelIds;
    }

    public void setKjoeptAndelIds(Set<AndelIMatrikkelenhetId<?>> kjoeptAndelIds) {
        this.kjoeptAndelIds.clear();
        this.kjoeptAndelIds.addAll(kjoeptAndelIds);
    }

    public Set<AndelIMatrikkelenhet> getKjoeptAndel() {
        return store().get(kjoeptAndelIds);
    }

    public Set<AndelIMatrikkelenhetId<?>> getSolgtAndelIds() {
        return solgtAndelIds;
    }

    public void setSolgtAndelIds(Set<AndelIMatrikkelenhetId<?>> solgtAndelIds) {
        this.solgtAndelIds.clear();
        this.solgtAndelIds.addAll(solgtAndelIds);
    }

    public Set<AndelIMatrikkelenhet> getSolgtAndel() {
        return store().get(solgtAndelIds);
    }

    public Set<Beloep> getVederlag() {
        return vederlag;
    }

    public void setVederlag(Set<Beloep> vederlag) {
        this.vederlag.clear();
        this.vederlag.addAll(vederlag);
    }
}
