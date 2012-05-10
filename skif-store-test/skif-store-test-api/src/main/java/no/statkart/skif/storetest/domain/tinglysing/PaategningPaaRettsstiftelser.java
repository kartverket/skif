package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRettsstiftelseRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public abstract class PaategningPaaRettsstiftelser extends Paategning {
    private Set<RettsstiftelseId<?>> gjelder = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.GJELDER_AKTIV);
    private Set<RettsstiftelseId<?>> gjelderHistorisk = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.GJELDER_HISTORISK);

    @Override
    public PaategningPaaRettsstiftelserId<?> getId() {
        return (PaategningPaaRettsstiftelserId<?>) super.getId();
    }

    public Set<RettsstiftelseId<?>> getGjelder() {
        return gjelder;
    }

    public void setGjelder(Set<RettsstiftelseId<?>> gjelder) {
        this.gjelder.clear();
        this.gjelder.addAll(gjelder);
    }

    public Set<RettsstiftelseId<?>> getGjelderHistorisk() {
        return gjelderHistorisk;
    }

    public void setGjelderHistorisk(Set<RettsstiftelseId<?>> gjelderHistorisk) {
        this.gjelderHistorisk.clear();
        this.gjelderHistorisk.addAll(gjelderHistorisk);
    }
}
