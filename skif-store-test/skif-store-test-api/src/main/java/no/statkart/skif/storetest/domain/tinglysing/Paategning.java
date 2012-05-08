package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRelasjonRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public abstract class Paategning extends Rettsstiftelse {
    private Set<RettsstiftelseRelasjonId<?>> paategnerIds = rettsstiftelseRelasjonIdsKoblinger.get(RettsstiftelseRelasjonRolle.PAATEGNER);
    private Set<RettsstiftelseRelasjonId<?>> paategnerHistoriskIds = rettsstiftelseRelasjonIdsKoblinger.get(RettsstiftelseRelasjonRolle.PAATEGNER);

    public Set<RettsstiftelseRelasjonId<?>> getPaategnerIds() {
        return paategnerIds;
    }

    public Set<RettsstiftelseRelasjon> getPaategner() {
        return store().get(paategnerIds);
    }

    public void setPaategnerIds(Set<RettsstiftelseRelasjonId<?>> paategnerIds) {
        this.paategnerIds.clear();
        this.paategnerIds.addAll(paategnerIds);
    }

    public Set<RettsstiftelseRelasjonId<?>> getPaategnerHistoriskIds() {
        return paategnerHistoriskIds;
    }

    public Set<RettsstiftelseRelasjon> getPaategnerHistorisk() {
        return store().get(paategnerHistoriskIds);
    }

    public void setPaategnerHistoriskIds(Set<RettsstiftelseRelasjonId<?>> paategnerHistoriskIds) {
        this.paategnerHistoriskIds.clear();
        this.paategnerHistoriskIds.addAll(paategnerHistoriskIds);
    }
}